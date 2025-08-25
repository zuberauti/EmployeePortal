
package com.employee.EmployeePortal.service;

import com.employee.EmployeePortal.dto.PayslipDTO;
import com.employee.EmployeePortal.dto.PayslipSummaryDTO;
import com.employee.EmployeePortal.entity.Employee;
import com.employee.EmployeePortal.entity.Payslip;
import com.employee.EmployeePortal.enums.PayslipStatus;
import com.employee.EmployeePortal.exception.ResourceNotFoundException;
import com.employee.EmployeePortal.repository.AttendanceRepository;
import com.employee.EmployeePortal.repository.EmployeeRepository;
import com.employee.EmployeePortal.repository.PayslipItemRepository;
import com.employee.EmployeePortal.repository.PayslipRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.internet.MimeMessage;
import com.itextpdf.text.Document;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PayslipServiceImpl implements PayslipService {

    private final PayslipRepository payslipRepository;
    private final PayslipItemRepository payslipItemRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final ModelMapper modelMapper;
    private final JavaMailSender mailSender;

    @Autowired
    public PayslipServiceImpl(PayslipRepository payslipRepository,
                              PayslipItemRepository payslipItemRepository,
                              EmployeeRepository employeeRepository,
                              AttendanceRepository attendanceRepository,
                              ModelMapper modelMapper,
                              JavaMailSender mailSender) {
        this.payslipRepository = payslipRepository;
        this.payslipItemRepository = payslipItemRepository;
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
        this.modelMapper = modelMapper;
        this.mailSender = mailSender;
    }

    @Override
    @Transactional
    public PayslipDTO generatePayslip(String employeeId, LocalDate payPeriodStart, LocalDate payPeriodEnd) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        // prevent duplicates for the same period
        if (!payslipRepository.findByEmployeeIdAndPayPeriodBetween(employeeId, payPeriodStart, payPeriodEnd).isEmpty()) {
            throw new RuntimeException("Payslip already exists for this pay period");
        }

        Payslip payslip = new Payslip();
        payslip.setEmployee(employee);
        payslip.setPayslipNumber(generatePayslipNumber());
        payslip.setPayPeriodStart(payPeriodStart);
        payslip.setPayPeriodEnd(payPeriodEnd);
        payslip.setPayDate(LocalDate.now());
        payslip.setStatus(PayslipStatus.PROCESSING);

        // demo calculations
        BigDecimal basicSalary = BigDecimal.valueOf(5000);
        BigDecimal overtimePay = BigDecimal.valueOf(250);

        payslip.setBasicSalary(basicSalary);
        payslip.setHousingAllowance(BigDecimal.valueOf(1000));
        payslip.setTransportAllowance(BigDecimal.valueOf(300));
        payslip.setMealAllowance(BigDecimal.ZERO);
        payslip.setBonus(BigDecimal.ZERO);
        payslip.setOtherAllowances(BigDecimal.ZERO);
        payslip.setOvertimePay(overtimePay);

        payslip.setTaxDeduction(basicSalary.multiply(BigDecimal.valueOf(0.15)));
        payslip.setSocialSecurity(basicSalary.multiply(BigDecimal.valueOf(0.05)));
        payslip.setHealthInsurance(BigDecimal.ZERO);
        payslip.setPensionContribution(BigDecimal.ZERO);
        payslip.setOtherDeductions(BigDecimal.ZERO);

        BigDecimal gross = basicSalary
                .add(payslip.getHousingAllowance())
                .add(payslip.getTransportAllowance())
                .add(overtimePay)
                .add(payslip.getMealAllowance())
                .add(payslip.getBonus())
                .add(payslip.getOtherAllowances());

        BigDecimal deductions = payslip.getTaxDeduction()
                .add(payslip.getSocialSecurity())
                .add(payslip.getHealthInsurance())
                .add(payslip.getPensionContribution())
                .add(payslip.getOtherDeductions());

        payslip.setGrossEarnings(gross);
        payslip.setTotalDeductions(deductions);
        payslip.setNetPay(gross.subtract(deductions));

        Payslip saved = payslipRepository.save(payslip);
        return convertToDTO(saved);
    }

    @Override
    public PayslipDTO getPayslipById(Long payslipId) {
        Payslip payslip = payslipRepository.findById(payslipId)
                .orElseThrow(() -> new ResourceNotFoundException("Payslip not found with id: " + payslipId));
        return convertToDTO(payslip);
    }

    @Override
    public List<PayslipSummaryDTO> getEmployeePayslips(String employeeId) {
        return payslipRepository.findByEmployeeIdOrderByPayPeriodEndDesc(employeeId).stream()
                .map(this::convertToSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    public byte[] generatePayslipPdf(Long payslipId) {
        Payslip payslip = payslipRepository.findById(payslipId)
                .orElseThrow(() -> new ResourceNotFoundException("Payslip not found"));

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            document.add(new Paragraph("Payslip", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20)));
            document.add(new Paragraph("Payslip Number: " + payslip.getPayslipNumber()));
            document.add(new Paragraph("Employee: " +
                    payslip.getEmployee().getFirstName() + " " + payslip.getEmployee().getLastName()));
            document.add(new Paragraph("Pay Period: " + payslip.getPayPeriodStart() +
                    " to " + payslip.getPayPeriodEnd()));
            document.add(new Paragraph("Pay Date: " + payslip.getPayDate()));
            document.add(new Paragraph("Status: " + payslip.getStatus()));
            document.add(new Paragraph("Basic Salary: " + payslip.getBasicSalary()));
            document.add(new Paragraph("Gross Earnings: " + payslip.getGrossEarnings()));
            document.add(new Paragraph("Total Deductions: " + payslip.getTotalDeductions()));
            document.add(new Paragraph("Net Pay: " + payslip.getNetPay()));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }

    @Override
    public void sendPayslipByEmail(Long payslipId, String recipientEmail) {
        Payslip payslip = payslipRepository.findById(payslipId)
                .orElseThrow(() -> new ResourceNotFoundException("Payslip not found"));

        byte[] pdfBytes = generatePayslipPdf(payslipId);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("yourgmail@gmail.com");
            helper.setTo(recipientEmail);
            helper.setSubject("Payslip - " + payslip.getPayslipNumber());
            helper.setText("Dear " + payslip.getEmployee().getFirstName() +
                    ",\n\nPlease find attached your payslip.\n\nThanks,\nHR Team");

            helper.addAttachment("Payslip-" + payslip.getPayslipNumber() + ".pdf",
                    new ByteArrayResource(pdfBytes));

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send payslip email", e);
        }
    }

    @Override
    @Transactional
    public PayslipDTO updatePayslip(Long payslipId, PayslipDTO dto) {
        Payslip existing = payslipRepository.findById(payslipId)
                .orElseThrow(() -> new ResourceNotFoundException("Payslip not found with id: " + payslipId));

        // map only scalar money fields to avoid touching relationships
        if (dto.getBasicSalary() != null) existing.setBasicSalary(dto.getBasicSalary());
        if (dto.getHousingAllowance() != null) existing.setHousingAllowance(dto.getHousingAllowance());
        if (dto.getTransportAllowance() != null) existing.setTransportAllowance(dto.getTransportAllowance());
        if (dto.getMealAllowance() != null) existing.setMealAllowance(dto.getMealAllowance());
        if (dto.getOvertimePay() != null) existing.setOvertimePay(dto.getOvertimePay());
        if (dto.getBonus() != null) existing.setBonus(dto.getBonus());
        if (dto.getOtherAllowances() != null) existing.setOtherAllowances(dto.getOtherAllowances());
        if (dto.getTaxDeduction() != null) existing.setTaxDeduction(dto.getTaxDeduction());
        if (dto.getSocialSecurity() != null) existing.setSocialSecurity(dto.getSocialSecurity());
        if (dto.getHealthInsurance() != null) existing.setHealthInsurance(dto.getHealthInsurance());
        if (dto.getPensionContribution() != null) existing.setPensionContribution(dto.getPensionContribution());
        if (dto.getOtherDeductions() != null) existing.setOtherDeductions(dto.getOtherDeductions());

        // recompute totals
        BigDecimal gross = n(existing.getBasicSalary())
                .add(n(existing.getHousingAllowance()))
                .add(n(existing.getTransportAllowance()))
                .add(n(existing.getOvertimePay()))
                .add(n(existing.getMealAllowance()))
                .add(n(existing.getBonus()))
                .add(n(existing.getOtherAllowances()));

        BigDecimal deductions = n(existing.getTaxDeduction())
                .add(n(existing.getSocialSecurity()))
                .add(n(existing.getHealthInsurance()))
                .add(n(existing.getPensionContribution()))
                .add(n(existing.getOtherDeductions()));

        existing.setGrossEarnings(gross);
        existing.setTotalDeductions(deductions);
        existing.setNetPay(gross.subtract(deductions));

        Payslip updated = payslipRepository.save(existing);
        return convertToDTO(updated);
    }

    @Override
    @Transactional
    public void deletePayslip(Long payslipId) {
        payslipItemRepository.deleteByPayslipId(payslipId);
        payslipRepository.deleteById(payslipId);
    }

    private String generatePayslipNumber() {
        return "PSL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private BigDecimal n(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    // === Manual, safe mapping (no ModelMapper on nested Employee) ===
    private PayslipDTO convertToDTO(Payslip p) {
        PayslipDTO dto = new PayslipDTO();
        dto.setId(p.getId());
        dto.setPayslipNumber(p.getPayslipNumber());
        dto.setPayPeriodStart(p.getPayPeriodStart());
        dto.setPayPeriodEnd(p.getPayPeriodEnd());
        dto.setPayDate(p.getPayDate());
        dto.setStatus(p.getStatus() != null ? p.getStatus().name() : null);

        dto.setBasicSalary(p.getBasicSalary());
        dto.setHousingAllowance(p.getHousingAllowance());
        dto.setTransportAllowance(p.getTransportAllowance());
        dto.setMealAllowance(p.getMealAllowance());
        dto.setOvertimePay(p.getOvertimePay());
        dto.setBonus(p.getBonus());
        dto.setOtherAllowances(p.getOtherAllowances());

        dto.setTaxDeduction(p.getTaxDeduction());
        dto.setSocialSecurity(p.getSocialSecurity());
        dto.setHealthInsurance(p.getHealthInsurance());
        dto.setPensionContribution(p.getPensionContribution());
        dto.setOtherDeductions(p.getOtherDeductions());

        dto.setGrossEarnings(p.getGrossEarnings());
        dto.setTotalDeductions(p.getTotalDeductions());
        dto.setNetPay(p.getNetPay());

        if (p.getEmployee() != null) {
            dto.setEmployeeId(p.getEmployee().getEmployeeId());
            dto.setEmployeeName(p.getEmployee().getFirstName() + " " + p.getEmployee().getLastName());
        }

        return dto;
    }

    private PayslipSummaryDTO convertToSummaryDTO(Payslip p) {
        PayslipSummaryDTO dto = new PayslipSummaryDTO();
        dto.setId(p.getId());
        dto.setPayslipNumber(p.getPayslipNumber());
        dto.setPayPeriodStart(p.getPayPeriodStart());
        dto.setPayPeriodEnd(p.getPayPeriodEnd());
        dto.setPayDate(p.getPayDate());
        dto.setStatus(p.getStatus() != null ? p.getStatus().name() : null);
        dto.setNetPay(p.getNetPay());
        return dto;
    }
}


