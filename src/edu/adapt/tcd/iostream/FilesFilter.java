package edu.adapt.tcd.iostream;

import java.io.File;
import java.io.FileFilter;

public class FilesFilter implements FileFilter {

	String[] okExtensions = null;
	public FilesFilter(String[] validExtensions)
	{
		this.okExtensions = validExtensions;
	}
	
	@Override
	public boolean accept(File file) {
		for (String goodExtension: this.okExtensions)
		{
			if (file.getName().toLowerCase().endsWith(goodExtension))
				return true;
		}
		return false;
	}

}
