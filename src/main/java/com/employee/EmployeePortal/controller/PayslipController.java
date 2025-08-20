//package com.employee.EmployeePortal.controller;
//
//import com.employee.EmployeePortal.dto.PayslipDTO;
//import com.employee.EmployeePortal.dto.PayslipSummaryDTO;
//import com.employee.EmployeePortal.service.PayslipService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.http.*;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/payslips")
//@CrossOrigin // remove or configure as needed
//public class PayslipController {
//
//    private final PayslipService payslipService;
//
//    @Autowired
//    public PayslipController(PayslipService payslipService) {
//        this.payslipService = payslipService;
//    }
//
//    /**
//     * Generate a payslip for an employee for a given period.
//     * Example:
//     *  POST /api/payslips/generate?employeeId=EMP001&payPeriodStart=2025-07-01&payPeriodEnd=2025-07-31
//     */
//    @PostMapping("/generate")
//    public ResponseEntity<PayslipDTO> generatePayslip(
//            @RequestParam String employeeId,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate payPeriodStart,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate payPeriodEnd
//    ) {
//        PayslipDTO payslip = payslipService.generatePayslip(employeeId, payPeriodStart, payPeriodEnd);
//        return ResponseEntity.status(HttpStatus.CREATED).body(payslip);
//    }
//
//    /**
//     * Get a single payslip by its id.
//     */
//    @GetMapping("/{payslipId}")
//    public ResponseEntity<PayslipDTO> getPayslip(@PathVariable Long payslipId) {
//        return ResponseEntity.ok(payslipService.getPayslipById(payslipId));
//    }
//
//    /**
//     * List payslips for one employee (newest first).
//     */
//    @GetMapping("/employee/{employeeId}")
//    public ResponseEntity<List<PayslipSummaryDTO>> getEmployeePayslips(@PathVariable String employeeId) {
//        return ResponseEntity.ok(payslipService.getEmployeePayslips(employeeId));
//    }
//
//    /**
//     * Download payslip as a PDF (currently a stub byte[] from the service).
//     * Frontend can trigger a file download directly.
//     */
//    @GetMapping("/{payslipId}/download")
//    public ResponseEntity<byte[]> downloadPayslip(@PathVariable Long payslipId) {
//        byte[] pdfBytes = payslipService.generatePayslipPdf(payslipId);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_PDF);
//        headers.setContentDisposition(ContentDisposition.attachment()
//                .filename("payslip_" + payslipId + ".pdf")
//                .build());
//
//        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
//    }
//
//    /**
//     * Email a payslip (service currently logs/stubs sending).
//     * If recipientEmail is omitted, your service can default to the employee's email.
//     */
//    @PostMapping("/{payslipId}/email")
//    public ResponseEntity<Void> emailPayslip(
//            @PathVariable Long payslipId,
//            @RequestParam(required = false) String recipientEmail
//    ) {
//        payslipService.sendPayslipByEmail(payslipId, recipientEmail != null ? recipientEmail : "");
//        return ResponseEntity.ok().build();
//    }
//
//    /**
//     * Update an existing payslip (recalculations handled in service).
//     */
//    @PutMapping("/{payslipId}")
//    public ResponseEntity<PayslipDTO> updatePayslip(
//            @PathVariable Long payslipId,
//            @RequestBody PayslipDTO payslipDTO
//    ) {
//        return ResponseEntity.ok(payslipService.updatePayslip(payslipId, payslipDTO));
//    }
//
//    /**
//     * Delete a payslip and its items.
//     */
//    @DeleteMapping("/{payslipId}")
//    public ResponseEntity<Void> deletePayslip(@PathVariable Long payslipId) {
//        payslipService.deletePayslip(payslipId);
//        return ResponseEntity.noContent().build();
//    }
//}
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
