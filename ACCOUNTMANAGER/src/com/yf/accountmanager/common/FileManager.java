package com.yf.accountmanager.common;

import java.io.File;
import java.io.FileFilter;
import java.util.Comparator;
import java.util.Locale;
import java.util.regex.Pattern;

import com.yf.accountmanager.util.FileUtils;

public class FileManager {
	public static final int TYPE_OPTION = 0, TYPE_SORT = 1, TYPE_SCREEN = 2;
	public static final String[] FILEOPTIONS = new String[] { "delete",
			"rename", "copy", "move", "detail" };
	
	public static final String[] SORTOPTIONS = new String[] { "名字 ↑", "名字↓",
			"大小↑", "大小↓", "修改日期↑", "修改日期↓" };
	
	public static final String[] SCREENOPTIONS = new String[] { "全部", "文件",
			"文件夹", "系统文件", "writeable" };

	public static class FileComparator implements Comparator<File> {
		public int type = 0;
		public Comparator<File> com;
		
		public FileComparator() {
			com=new Comparator<File>() {
				public int compare(File left, File right) {
					return  left.getName().compareToIgnoreCase(right.getName());
				}
			};
		}

		public boolean setType(int type) {
			if (this.type == type)
				return false;
			this.type = type;
			switch (type) {
			case 0:
				com=new Comparator<File>() {
					public int compare(File left, File right) {
						return  left.getName().compareToIgnoreCase(right.getName());
					}
				};
				
				break;
			case 1:
				com=new Comparator<File>() {
					public int compare(File left, File right) {
						return  right.getName().compareToIgnoreCase(left.getName());
					}
				};
				break;
			case 2:
				com=new Comparator<File>() {
					public int compare(File left, File right) {
						int delta=0;
						if (left.isDirectory()) {
							delta = left.getName().compareToIgnoreCase(right.getName());
						} else{
							long gap=  left.length() - right.length();
							delta=longToInt(gap);
						}
						
						return delta;
					}
				};
				

				/*
				 * delta = (int) (FileUtils.getDirBytes(left) - FileUtils
				 * .getDirBytes(right));
				 */
				break;
			case 3:
				
				com=new Comparator<File>() {
					public int compare(File left, File right) {
						int delta=0;
						if (left.isDirectory()) {
							delta = left.getName().compareToIgnoreCase(right.getName());
						} else{
							long gap=(right.length() - left.length());
							delta=longToInt(gap);
						}
						return delta;
					}
				};

			
				/*
				 * delta = (int) (FileUtils.getDirBytes(right) - FileUtils
				 * .getDirBytes(left));
				 */
				break;
			case 4:
				com=new Comparator<File>() {
					public int compare(File left, File right) {
						long gap= (left.lastModified() - right.lastModified());
						return longToInt(gap);
					}
				};
			
				break;
			case 5:
				com=new Comparator<File>() {
					public int compare(File left, File right) {
						long gap= (right.lastModified() - left.lastModified());
						return longToInt(gap);
					}
				};
				break;
			}
			return true;
		}
		
		public int longToInt(long l){
			return l>0?1:l<0?-1:0;
		}
		
		public int compareFile(File left,File right){
			int delta = compareFileType(left, right);
			if (delta != 0)
				return delta;
			return com.compare(left, right);
			
		}

		public int compare(File left, File right) {
			return compareFile(left, right);
		}
	}
	
	
	
	public static class IFileFilter implements FileFilter {
		public int type = 0;

		public boolean setType(int type) {
			if (this.type == type)
				return false;
			this.type = type;
			return true;
		}

		public boolean accept(File file) {
			boolean b = true;
			switch (type) {
			case 0:
				b = true;
				break;
			case 1:
				b = file.isFile();
				break;
			case 2:
				b = file.isDirectory();
				break;
			case 3:
				b = !file.isFile() && !file.isDirectory();
				break;
			case 4:
				b = file.canWrite();
				break;
			}
			return b;
		}
	}

	public static int getFileType(File file) {
		return file.isDirectory() ? 0 : file.isFile() ? 1 : 2;
	}

	public static int compareFileType(File left, File right) {
		return getFileType(left) - getFileType(right);
	}
	
	
	
	
	
	
	/**
	 * 
	 * @author Administrator
	 *
	 */
	

	public static class ConditionalSearchFileFilter implements FileFilter{
		public boolean caseSensitive,regularExpression,writeable,readable,
							
							file,directory,systemFile,sizeRestriction,timeRestriction;
		
		public long gtSize,ltSize,gtTime,ltTime;
		
		public String key;
		
		public Pattern pattern;
						
		
		public static boolean accept(File file,String key){
			String name=file.getName();
			return name.toUpperCase(Locale.CHINA).contains(key.toUpperCase(Locale.CHINA));
		}
		

		public boolean accept(File file) {
			if(isTypeMatch(file)&&inTimeScope(file)&&inSizeScope(file)
					&&isReadabilitySatisfied(file)&&isWriteabilitySatisfied(file)){
				String name=file.getName();
				if(regularExpression){
					if(caseSensitive){
						if(pattern==null)
							pattern=Pattern.compile("^"+key+"$",0);
						return pattern.matcher(name).matches();
//						return name.matches("^"+key+"$");
					}else{
						if(pattern==null)
							pattern=Pattern.compile("(?i)^"+key+"$",0);
						return pattern.matcher(name).matches();
//						return name.matches("(?i)^"+key+"$");
					}
				}else if(caseSensitive){
					return name.contains(key);
				}else{
					return name.toUpperCase(Locale.CHINA).contains(key.toUpperCase(Locale.CHINA));
				}
			}
			return false;
		}
		
		public boolean isWriteabilitySatisfied(File file){
			if(writeable)
				return file.canWrite();
			return true;
			
		}
		public boolean isReadabilitySatisfied(File file){
			if(readable)
				return file.canRead();
			return true;
		}
		
		private boolean isTypeMatch(File f){
			if(file&&directory&&systemFile) return true;
			if(!(file||directory||systemFile)) return true;
//			boolean b = false;
//			b|=file&f.isFile();
//			b|=directory&f.isDirectory();
//			b|=systemFile&FileUtils.isSystemFile(f);
			if(file&f.isFile()) 
				return true;
			if(directory&f.isDirectory())
				 return true;
			if(systemFile&FileUtils.isSystemFile(f))
				return true;
			return false;
			
		}
		
		private boolean inTimeScope(File file){
			if(gtTime==0&&ltTime==0)
				return true;
			if(timeRestriction){
				long l=file.lastModified();
				if(gtTime==0)
					return l<=ltTime;
				if(gtTime>=ltTime)
					return l>=gtTime;
				return l>=gtTime&&l<=ltTime;
			}
			return true;
		}
		
		private boolean inSizeScope(File file){
			if(file.isDirectory()||(gtSize==0&&ltSize==0)) return true;
			if(sizeRestriction){
				long l=file.length();
				
				if(gtSize>ltSize)
					return l>=gtSize;
				
				return l>=gtSize&&l<=ltSize;
			}
			return true;
		}
		
	}
	
	
}
