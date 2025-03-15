package com.spring.ekart.service;

import java.io.IOException;
import java.util.List;
import java.util.Random;


import com.spring.ekart.dto.Product;
import com.spring.ekart.dto.Vendor;
import com.spring.ekart.helper.AES;
import com.spring.ekart.helper.CloudinaryHelper;
import com.spring.ekart.helper.EmailSender;
import com.spring.ekart.repository.ItemRepository;
import com.spring.ekart.repository.ProductRepository;
import com.spring.ekart.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Service
public class VendorService {

	@Autowired
	VendorRepository vendorRepository;
	
	@Autowired
	EmailSender emailSender;

	@Autowired
	CloudinaryHelper cloudinaryHelper;
	
	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	ItemRepository itemRepository;
	public String loadRegistration(ModelMap map, Vendor vendor) {
		map.put("vendor", vendor);
		return "vendor-register.html";
	}

	public String registration(@Valid Vendor vendor, BindingResult result,HttpSession session) {
		if (!vendor.getPassword().equals(vendor.getConfirmPassword()))
			result.rejectValue("confirmPassword", "error.confirmPassword",
					"* Password and Confirm Password Should Match");
		if (vendorRepository.existsByEmail(vendor.getEmail()))
			result.rejectValue("email", "error.email", "* Email Already Exists");
		if (vendorRepository.existsByMobile(vendor.getMobile()))
			result.rejectValue("mobile", "error.mobile", "* Mobile Number Already Exists");

		if (result.hasErrors())
			return "vendor-register.html";
		else {
			int otp=new Random().nextInt(100000, 1000000);
			vendor.setOtp(otp);
			vendor.setPassword(AES.encrypt(vendor.getPassword()));
			vendorRepository.save(vendor);
			//email Logic
			emailSender.send(vendor);
			System.err.println(vendor.getOtp());
			session.setAttribute("success", "Otp Sent Successfully");
			return "redirect:/vendor/otp/"+vendor.getId();
		}
	}

	public String verifyOtp(int id, int otp,HttpSession session) {
		Vendor vendor=vendorRepository.findById(id).orElseThrow();
		if(vendor.getOtp()==otp) {
			vendor.setVerified(true);
			vendorRepository.save(vendor);
			session.setAttribute("success", "Vendor Account Created Success");
			return "redirect:/";
		}else {
			session.setAttribute("failure", "OTP Missmatch");
			return "redirect:/vendor/otp/"+vendor.getId();
		}
	}

	public String login(String email, String password, HttpSession session) {
		Vendor vendor=vendorRepository.findByEmail(email);
		if(vendor==null) {
			session.setAttribute("failure", "Invalid Email");
			return "redirect:/vendor/login";
		}else {
			if(AES.decrypt(vendor.getPassword()).equals(password)) {
				if(vendor.isVerified()) {
					session.setAttribute("vendor", vendor);
					session.setAttribute("success", "Login Success");
					return "redirect:/vendor/home";
				}else {
					int otp = new Random().nextInt(100000, 1000000);
					vendor.setOtp(otp);
					vendorRepository.save(vendor);
					//emailSender.send(vendor);
					System.err.println(vendor.getOtp());
					session.setAttribute("success", "Otp Sent Successfully, First Verify Email for Logging In");
					return "redirect:/vendor/otp/" + vendor.getId();
				}
			}else {
				session.setAttribute("failure", "Invalid Password");
				return "redirect:/vendor/login";
			}
		}
	}
	public String loadOtpPage(int id, ModelMap map) {
		map.put("id", id);
		return "vendor-otp.html";
	}
	public String loadHome(HttpSession session) {
		if (session.getAttribute("vendor") != null)
			return "vendor-home.html";
		else {
			session.setAttribute("failure", "Invalid Session, First Login");
			return "redirect:/vendor/login";
		}
	}
	public String addProduct(HttpSession session) {
		if (session.getAttribute("vendor") != null)
			return "add-product.html";
		else {
			session.setAttribute("failure", "Invalid Session, First Login");
			return "redirect:/vendor/login";
		}
	}
	public String addProduct(Product product, HttpSession session) throws IOException {
		if (session.getAttribute("vendor") != null) {
			Vendor vendor = (Vendor) session.getAttribute("vendor");
			product.setVendor(vendor);
			product.setImageLink(cloudinaryHelper.saveToCloudinary(product.getImage()));
			productRepository.save(product);
			session.setAttribute("success", "Product Added Success");
			return "redirect:/vendor/home";
		} else {
			session.setAttribute("failure", "Invalid Session, First Login");
			return "redirect:/vendor/login";
		}
	}
	public String manageProducts(HttpSession session, ModelMap map) {
		if (session.getAttribute("vendor") != null) {
			Vendor vendor = (Vendor) session.getAttribute("vendor");
			List<Product> products = productRepository.findByVendor(vendor);
			if (products.isEmpty()) {
				session.setAttribute("failure", "No Products Present");
				return "redirect:/vendor/home";
			} else {
				map.put("products", products);
				return "vendor-view-products.html";
			}
		} else {
			session.setAttribute("failure", "Invalid Session, First Login");
			return "redirect:/vendor/login";
		}
	}
	public String deleteProduct(int id, HttpSession session) {
		if (session.getAttribute("vendor") != null) {
			productRepository.deleteById(id);
			session.setAttribute("success", "Product Deleted Success");
			return "redirect:/manage-products";
		} else {
			session.setAttribute("failure", "Invalid Session, First Login");
			return "redirect:/vendor/login";
		}
	}
	public String editProdcut(int id, ModelMap map, HttpSession session) {
		if (session.getAttribute("vendor") != null) {
			Product product = productRepository.findById(id).get();
			map.put("product", product);
			return "edit-product.html";
		} else {
			session.setAttribute("failure", "Invalid Session, First Login");
			return "redirect:/vendor/login";
		}
	}
	public String updateProduct(Product product, HttpSession session) throws IOException {
		if (session.getAttribute("vendor") != null) {
			Vendor vendor = (Vendor) session.getAttribute("vendor");
			product.setImageLink(cloudinaryHelper.saveToCloudinary(product.getImage()));
			product.setVendor(vendor);
			productRepository.save(product);
//			Item item = itemRepository.findByNameLike(product.getName()).get(0);
//			item.setPrice(product.getPrice());
//			itemRepository.save(item);
			session.setAttribute("success", "Product Updated Success");
			return "redirect:/manage-products";
		} else {
			session.setAttribute("failure", "Invalid Session, First Login");
			return "redirect:/vendor/login";
		}
	}

}