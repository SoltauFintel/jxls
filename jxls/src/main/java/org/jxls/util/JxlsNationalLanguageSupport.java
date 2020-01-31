package org.jxls.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public abstract class JxlsNationalLanguageSupport {
    private String start = "R{";
    private String end = "}";
    private String defaultValueDelimiter = "=";
    protected boolean changed;
    
    /**
     * @param in XLSX file
     * @return new temp file. Caller must delete it.
     */
    public File process(File in) {
        try {
            File out = File.createTempFile("JXLS-R-", ".xlsx");
            process(in, out);
            return out;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param in XLSX file that contain R{key} elements
     * @param out XLSX file with translated R{key} elements
     * @throws IOException
     */
    public void process(File in, File out) throws IOException {
        try (ZipFile zipFile = new ZipFile(in); ZipOutputStream zipout = new ZipOutputStream(new FileOutputStream(out))) {
            for (Enumeration<? extends ZipEntry> entries = zipFile.entries(); entries.hasMoreElements(); ) {
                processZipEntry(entries.nextElement(), zipFile, zipout);
            }
        }
    }

    protected void processZipEntry(ZipEntry zipEntry, ZipFile zipin, ZipOutputStream zipout) throws IOException {
        boolean copy = true;
        zipout.putNextEntry(new ZipEntry(zipEntry.getName()));
        if (zipEntry.getName().toLowerCase().endsWith(".xml")) {
            try (InputStream bis = new BufferedInputStream(zipin.getInputStream(zipEntry))) {
                int len = bis.available();
                if (len > 0) {
                    byte[] buffer = new byte[len];
                    bis.read(buffer, 0, len);

                    byte result[] = translateAll(new String(buffer)).getBytes();

                    zipout.write(changed ? result : buffer);
                    copy = false;
                }
            }
        }
        if (copy) {
            copy(zipEntry, zipin, zipout);
        }
        zipout.closeEntry();
    }

    protected String translateAll(String xml) {
        changed = false;
        if (xml.contains(start)) {
            int o = xml.indexOf(start);
            int oo = xml.indexOf(end, o + start.length());
            while (o >= 0 && oo > o) {
                String name = xml.substring(o + start.length(), oo);
                String org = name;
                String fallback = name;
                if (name.contains(defaultValueDelimiter)) {
                    fallback = name.substring(name.indexOf(defaultValueDelimiter) + 1);
                    name = name.substring(0, name.indexOf(defaultValueDelimiter));
                }
                String result = translate(name, fallback);
                if (!org.equals(result)) {
                    changed = true;
                }

                xml = xml.substring(0, o) + result + xml.substring(oo + end.length());

                o = xml.indexOf(start);
                oo = xml.indexOf(end, o + start.length());
            }
        }
        return xml;
    }

    protected abstract String translate(String name, String fallback);

    protected void copy(ZipEntry zipEntry, ZipFile zipin, ZipOutputStream zipout) throws IOException {
        InputStream is = zipin.getInputStream(zipEntry);
        byte[] buf = new byte[8192];
        int len;
        while ((len = is.read(buf)) > 0) {
            zipout.write(buf, 0, len);
        }
    }
    
    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getDefaultValueDelimiter() {
        return defaultValueDelimiter;
    }

    public void setDefaultValueDelimiter(String defaultValueDelimiter) {
        this.defaultValueDelimiter = defaultValueDelimiter;
    }
}
