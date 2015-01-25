package com.alipay.automation.utils;

import java.io.File;
import java.io.IOException;

import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;

public class GZipUtils {
	public static void zipDirectory(File source, File destination, String name)
			throws IOException, IllegalArgumentException {
		Archiver archiver = ArchiverFactory.createArchiver(ArchiveFormat.TAR,
				CompressionType.GZIP);
		archiver.create(name, destination, source);
	}

}
