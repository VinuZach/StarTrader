package com.example.startraders.Printer;

import android.content.Intent;

import com.android.print.sdk.PrinterInstance;

public interface IPrinterOpertion {
	public void open(Intent data);
	public void close();
	public void chooseDevice();
	public PrinterInstance getPrinter();
}
