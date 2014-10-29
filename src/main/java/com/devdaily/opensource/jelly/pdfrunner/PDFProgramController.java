package com.devdaily.opensource.jelly.pdfrunner;

import java.io.IOException;

/**
 * This program was created extensively from an example in the book "More Java Pitfalls".
 * From what I have seen thus far, I highly recommend that book.
 */
public class PDFProgramController
{
  private String osName;
  private String[] cmd;

  public static void main(String args[])
  {
    PDFProgramController ppc = new PDFProgramController();
    ppc.runSampleProgram();
  }

  private void runSampleProgram()
  {
    try
    {
      if (osName.equals("Windows NT") || osName.equals("Windows 2000"))
      {
        cmd[0] = "cmd.exe";
        cmd[1] = "/C";
        cmd[2] = "c:/xfer/Latex/do_pdf.bat";
        cmd[3] = "littletest";
      }
      else if (osName.equals("Windows 95"))
      {
        cmd[0] = "command.com";
        cmd[1] = "/C";
        cmd[2] = "c:/xfer/Latex/do_pdf.bat";
        cmd[3] = "littletest";
      }

      runPDFProgram();

    }
    catch (Throwable t)
    {
      t.printStackTrace();
    }
  }

  public PDFProgramController(String[] cmd)
  {
    this.cmd = cmd;
    osName = System.getProperty("os.name");
  }

  /**
   * Used for testing only, so keep it private.
   */
  private PDFProgramController()
  {
    cmd = new String[4];
    osName = System.getProperty("os.name");
  }

  public void runPDFProgram()
  throws IOException, InterruptedException
  {
    Runtime rt = Runtime.getRuntime();
    //System.out.println("Execing " + cmd[0] + " " + cmd[1] + " " + cmd[2] + " " + cmd[3]);
    Process proc = rt.exec(cmd);

    // read any error output
    ThreadedStreamReader errorStream = new ThreadedStreamReader(proc.getErrorStream(), "ERROR");

    // read any standard output
    ThreadedStreamReader outputStream = new ThreadedStreamReader(proc.getInputStream(), "OUTPUT");

    // kick them off
    errorStream.start();
    outputStream.start();

    // any error???
    int exitVal = proc.waitFor();
    System.out.println("ExitValue: " + exitVal);
  }
}
