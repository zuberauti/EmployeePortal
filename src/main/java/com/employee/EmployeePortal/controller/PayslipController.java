
package com.employee.EmployeePortal.controller;

import com.employee.EmployeePortal.dto.EmailRequestDTO;
import com.employee.EmployeePortal.dto.PayslipDTO;
import com.employee.EmployeePortal.dto.PayslipSummaryDTO;
import com.employee.EmployeePortal.service.PayslipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/payslips")
public class PayslipController {

    private final PayslipService payslipService;

    @Autowired
    public PayslipController(PayslipService payslipService) {
        this.payslipService = payslipService;
    }

    // 1) Generate payslip
    @PostMapping("/generate")
    public ResponseEntity<PayslipDTO> generatePayslip(
            @RequestParam String employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate payPeriodStart,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate payPeriodEnd) {
        PayslipDTO payslip = payslipService.generatePayslip(employeeId, payPeriodStart, payPeriodEnd);
        return ResponseEntity.status(HttpStatus.CREATED).body(payslip);
    }

    // 2) Get single payslip
    @GetMapping("/{payslipId}")
    public ResponseEntity<PayslipDTO> getPayslip(@PathVariable Long payslipId) {
        return ResponseEntity.ok(payslipService.getPayslipById(payslipId));
    }

    // 3) Employee payslips
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<PayslipSummaryDTO>> getEmployeePayslips(@PathVariable String employeeId) {
        return ResponseEntity.ok(payslipService.getEmployeePayslips(employeeId));
    }

    // 4) Download PDF
    @GetMapping("/{payslipId}/download")
    public ResponseEntity<byte[]> downloadPayslip(@PathVariable Long payslipId) {
        byte[] pdfBytes = payslipService.generatePayslipPdf(payslipId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename("payslip_" + payslipId + ".pdf").build());
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    // 5) Email payslip
    @PostMapping("/{payslipId}/email")
    public ResponseEntity<String> emailPayslip(
            @PathVariable Long payslipId,
            @RequestBody EmailRequestDTO request) {

        payslipService.sendPayslipByEmail(payslipId, request.getRecipientEmail());
        return ResponseEntity.ok("Payslip sent successfully to " + request.getRecipientEmail());
    }
    // 6) Update payslip
    @PutMapping("/{payslipId}")
    public ResponseEntity<PayslipDTO> updatePayslip(
            @PathVariable Long payslipId,
            @RequestBody PayslipDTO payslipDTO) {
        return ResponseEntity.ok(payslipService.updatePayslip(payslipId, payslipDTO));
    }

    // 7) Delete payslip
    @DeleteMapping("/{payslipId}")
    public ResponseEntity<Void> deletePayslip(@PathVariable Long payslipId) {
        payslipService.deletePayslip(payslipId);
        return ResponseEntity.noContent().build();
    }
}
