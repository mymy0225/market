package com.example.market.forms;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class UpdateImageForm {

	@NotNull
	private MultipartFile image;
	
	@AssertTrue(message="画像ファイルをアップロードしてください")
	private boolean isNotUploaded() {
		if(!image.isEmpty()) {
			return true;
		}
		return false;
	}
	
	@AssertTrue(message="ファイルの形式はjpg、jpeg、pngを指定してください")
	private boolean isCollect() {
		if(image.isEmpty()) {
			return true;
		}
		if(isPermittedFile()) {
			return true;
		}
		return false;
	}
	
	@AssertTrue(message="アップロードする画像の幅、高さは 50px以上 1000px以内にしてください")
	private boolean isMatchSize() {
		if(image.isEmpty()) {
			return true;
		}

		if(!isPermittedFile()) {
			return true;
		}
		
		try{
			File tmpFile=new File(image.getOriginalFilename());
			tmpFile.createNewFile();
			FileOutputStream fos=new FileOutputStream(tmpFile);
			fos.write(image.getBytes());
			fos.close();
			if(tmpFile!=null && tmpFile.isFile()) {
				BufferedImage bi=ImageIO.read(tmpFile);
				int width=bi.getWidth();
				int height=bi.getHeight();
				tmpFile.delete();
				if(bi!=null) {
					if(50<=width && width<=1000 && 50<=height && height<=1000){
						return true;
					}
					return false;
				}
			}
			return false;
		}catch(IOException e) {
			e.printStackTrace();
		}return false;
	}
	
	private boolean isPermittedFile() {
		String ext=FilenameUtils.getExtension(image.getOriginalFilename());
		List<String> extList=Arrays.asList("jpg","jpeg","png");
		if(extList.contains(ext)) {
			return true;
		}return false;
	}
}