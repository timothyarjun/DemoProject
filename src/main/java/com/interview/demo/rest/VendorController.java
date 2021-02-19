package com.interview.demo.rest;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.interview.demo.dto.ExcelExporter;
import com.interview.demo.dto.VendorCategoryDto;
import com.interview.demo.entity.Booking;
import com.interview.demo.entity.Category;
import com.interview.demo.entity.Vendor;
import com.interview.demo.service.VendorService;
import com.interview.demo.util.OtpSystem;
@RestController
@RequestMapping("/api")
public class VendorController {
	@Autowired
	private VendorService vendorService;
	
	@PostMapping("/saveVendor")
	public Vendor saveVendor(@RequestBody Vendor vendor) {
		return vendorService.saveVendor(vendor);
	}
	
	@PostMapping("/saveCategory/{vendorId}")
	public Category saveCategory(@PathVariable long vendorId, @RequestBody Category category) {
		return vendorService.addCategory(vendorId,category);
	}
	
	@GetMapping("/getOne/{id}")
	public Vendor getOne(@PathVariable long id) {
		return vendorService.getOne(id);
	}
	
	@GetMapping("findCategory/{id}")
	public Category findOne(@PathVariable long id) {
		return vendorService.findCategory(id);
	}
	
	@PostMapping("/booking/{vendorId}/{categoryId}")
	public VendorCategoryDto booking(@PathVariable long vendorId, @PathVariable long categoryId, @RequestBody Booking book) throws Exception {
		return vendorService.booking(vendorId,categoryId,book);
	}
	
	@GetMapping("/listVendor")
	public List<Vendor> listAll(){
		return vendorService.listAll();
	}
	
	@GetMapping("/exportExcel")
	public String exportExcel(HttpServletResponse response) throws IOException {
		response.setContentType("application/octet-stream");
		String headkey = "Content-Disposition";
		
		SimpleDateFormat dataFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String date = dataFormatter.format(new Date());
		String fileName = "Vendor_"+ date + ".xlsx";
		String headvalue = "attachement; filename=" + fileName;
		response.setHeader(headkey, headvalue);
		
		ExcelExporter exporter = new ExcelExporter(listAll());
		exporter.export(response);
		
		return "file created successfully....";
		
	}
	
	@PostMapping("/excelImport")
	public String dateFromExcel(@RequestParam("efile") MultipartFile file) throws InvalidFormatException, EncryptedDocumentException, IOException{
		vendorService.ReadDateFromExcel(file.getOriginalFilename());
		return "Excel Form Inserted...";
	}
	
	@PostMapping("/sendMail")
	public String sendMail(@RequestParam MultipartFile file, @RequestParam String toAddress) throws MessagingException {
		return vendorService.sendMail(file, toAddress);
	}
	
	@PostMapping("/sendOtp/{mobileNumber}/otp")
	public String sendOtp(@PathVariable String mobileNumber) throws Exception {
		return vendorService.sendOtp(mobileNumber);
	}
	
	@PutMapping("/validateOtp/{mobileNumber}")
	public String validateOtp(@PathVariable String mobileNumber,@RequestBody OtpSystem otpSystem) throws Exception {
		return vendorService.validateOtp(mobileNumber, otpSystem);
	}
	
}
