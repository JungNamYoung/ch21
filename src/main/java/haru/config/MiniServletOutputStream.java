package haru.config;

import java.io.IOException;
import java.io.OutputStream;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;

public class MiniServletOutputStream extends ServletOutputStream {

  private OutputStream outputStream;

  public MiniServletOutputStream(OutputStream outputStream) {
    this.outputStream = outputStream;
  }

  @Override
  public void write(int b) throws IOException {
    outputStream.write(b);
  }

  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    outputStream.write(b, off, len);
  }

  @Override
  public void flush() throws IOException {
    outputStream.flush();
  }

  @Override
  public void close() throws IOException {
    outputStream.close();
  }

  @Override
  public boolean isReady() {
    return true;
  }

  @Override
  public void setWriteListener(WriteListener writeListener) {
    throw new UnsupportedOperationException("Not implemented");
  }
}