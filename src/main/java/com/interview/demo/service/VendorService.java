package com.interview.demo.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.print.attribute.standard.MediaSize.Other;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.interview.demo.dto.VendorCategoryDto;
import com.interview.demo.entity.Booking;
import com.interview.demo.entity.Category;
import com.interview.demo.entity.Vendor;
import com.interview.demo.repository.BookRepo;
import com.interview.demo.repository.CategoryRepo;
import com.interview.demo.repository.VendorRepo;
import com.interview.demo.util.MyComparator;
import com.interview.demo.util.OtpSystem;
import com.interview.demo.util.RandomNumber;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Service
public class VendorService {
	
	private final static String ACCOUNT_SID="ACa38a0a3a475bf47931f33caf0243efa2";
	private final static String AUTH_TOKEN="d6976b6279f9373d14397f7842bc6fe0";
	
	static {
		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
	}
	
	HashMap<String, OtpSystem> otp_set = new HashMap<String, OtpSystem>();
	
	private JavaMailSender javaMailSender;
	private VendorRepo vendorRepo;
	private CategoryRepo categoryRepo;
	private BookRepo bookRepo;

	@Autowired
	public VendorService(JavaMailSender javaMailSender, VendorRepo vendorRepo, CategoryRepo categoryRepo,
			BookRepo bookRepo) {
		super();
		this.javaMailSender = javaMailSender;
		this.vendorRepo = vendorRepo;
		this.categoryRepo = categoryRepo;
		this.bookRepo = bookRepo;
	}

	@Value("${spring.mail.username}")
	private String fromAddress;

	private String subject = "Test mail";

	private String body = "hi, This is your OTP " + randomGenerator();
	
	// generate random number
	public static String randomGenerator() {
		long min= 100000;
		long max= 200000;
		
		long randNum = (long)(Math.random()*(max-min+1)+min); 
		
		return Long.toString(randNum);
	}

	public Vendor saveVendor(Vendor vendor) {
		return vendorRepo.save(vendor);
	}

	public Category addCategory(long vendorId, Category category) {
		if (vendorId != 0) {
			category.setVendor(getOne(vendorId));
		}
		return categoryRepo.save(category);
	}

	public Vendor getOne(long id) {
		return vendorRepo.getOne(id);
	}

	public VendorCategoryDto booking(long vendorId, long categoryId, Booking book) throws Exception {
		VendorCategoryDto vendorDto = new VendorCategoryDto();
		Vendor v = getOne(vendorId);
		Category c = findCategory(categoryId);
		if (v != null && c != null) {
			Booking b = new Booking();
			b.setCategoryId(categoryId);
			b.setVendorId(vendorId);
			b.setRegisterDate(book.getRegisterDate());
			b.setId(book.getId());
			bookRepo.save(b);

			vendorDto.setName(v.getName());
			vendorDto.setPhone(v.getPhone());
			vendorDto.setCategoryName(c.getCategoryName());
			vendorDto.setPrice(c.getPrice());
			vendorDto.setRegisterDate(book.getRegisterDate());
			vendorDto.setBookId(b.getId());
			return vendorDto;
		} else {
			throw new Exception("vendor and service mismatch");
		}

	}

	public Category findCategory(long id) {
		return categoryRepo.getOne(id);
	}

	public List<Vendor> listAll() {
		List<Vendor> vendorList = vendorRepo.findAll();
		Collections.sort(vendorList, new MyComparator());
		return vendorList;
	}

	public void ReadDateFromExcel(String file) throws EncryptedDocumentException, InvalidFormatException, IOException {
		File f = new File(file);
		System.out.println(f);
		Workbook workbook = WorkbookFactory.create(f);
		System.out.println(workbook.getNumberOfSheets());
		for (Sheet sheet : workbook) {
			System.out.println(sheet.getSheetName());
			for (Row row : sheet) {
				long id = (long) row.getCell(0).getNumericCellValue();
				String name = row.getCell(1).getStringCellValue();
				long phone = (long) row.getCell(2).getNumericCellValue();

				System.out.println(id);
				System.out.println(name);
				System.out.println(phone);
//				Vendor v = new Vendor();
//				v.setId(id);
//				v.setName(name);
//				v.setPhone(phone);
//				vendorRepo.save(v);
			}
		}
	}

	public String sendMail(MultipartFile file, String toAddress) throws MessagingException {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
		helper.setFrom(fromAddress);
		helper.setTo(toAddress);
		helper.setSubject(subject);
		helper.setText(body);

		if (file.getOriginalFilename() != null) {
			helper.addAttachment(file.getOriginalFilename(), new InputStreamSource() {

				@Override
				public InputStream getInputStream() throws IOException {
					return file.getInputStream();
				}
			});
		}
		javaMailSender.send(mimeMessage);
		return "Mail sent successfully....";
	}

	public String sendOtp(String mobileNumber) throws Exception {
		if(mobileNumber!= null && mobileNumber.length()==10) {
			OtpSystem otpsystem= new OtpSystem();
			otpsystem.setMobileNumber(mobileNumber);
			otpsystem.setOtp(randomGenerator());
			otpsystem.setExpirytime(System.currentTimeMillis()+30000);
			
			otp_set.put(mobileNumber, otpsystem);
			
			Message.creator(new PhoneNumber("+918778926957"), new PhoneNumber("+13343101308"), "This is Your OTP "+otpsystem.getOtp()).create();
			return "OTP sent successfully....";
		}
		else {
			throw new Exception("Please provide valid mobile number...");
		}
	}

	public String validateOtp(String mobileNumber,OtpSystem otpSystem) throws Exception {
		if(otpSystem.getOtp()==null && otpSystem.getOtp().trim().length()<=0) {
			throw new Exception("Please Provide Otp here...");
		}
		if(otp_set.containsKey(mobileNumber)) {
			OtpSystem otpSystem1 = otp_set.get(mobileNumber);
			if(otpSystem1 !=null && otpSystem1.getExpirytime()>=System.currentTimeMillis()) {
				if(otpSystem1.getOtp().equals(otpSystem.getOtp())) {
					otp_set.remove(mobileNumber);
					return "Otp successfully validated...";
				} 
				else {
					throw new Exception("Incorrect OTP....");
				}
			}
			else {
				throw new Exception("OTP is expired....");
			}
		}
		else {
			throw new Exception("Please get the Otp in your number..");
		}
	}

}
