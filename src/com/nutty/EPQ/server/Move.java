package com.nutty.EPQ.server;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class Move extends Thread {

	
	/*
	 * This is the class that deals with Moving the motors
	 * @author Tom Hazell
	 * (C) 2014 Tom Hazell
	 */
	
	
	public boolean keepgoing = true;
	public static boolean update = true;
	private static int front = 1;
	private static int side = 1;

	public void doit(int[] Array) {

		front = Array[0];
		side = Array[1];
		update = true;

	}

	public void setupp() {

	}

	public void run() {

		// ping 16 front, 15 back, 1 on
		final GpioController gpio = GpioFactory.getInstance();
		final GpioPinDigitalOutput fOn = gpio.provisionDigitalOutputPin(
				RaspiPin.GPIO_01, "FrontOn", PinState.HIGH);
		final GpioPinDigitalOutput IOback = gpio.provisionDigitalOutputPin(
				RaspiPin.GPIO_15, "Back", PinState.LOW);
		final GpioPinDigitalOutput IOfront = gpio.provisionDigitalOutputPin(
				RaspiPin.GPIO_16, "Front", PinState.LOW);

		// pin 6 on. 10 left, 11 right.
		final GpioPinDigitalOutput tOn = gpio.provisionDigitalOutputPin(
				RaspiPin.GPIO_06, "turnOn", PinState.HIGH);
		final GpioPinDigitalOutput IOleft = gpio.provisionDigitalOutputPin(
				RaspiPin.GPIO_10, "Left", PinState.LOW);
		final GpioPinDigitalOutput IOright = gpio.provisionDigitalOutputPin(
				RaspiPin.GPIO_11, "Right", PinState.LOW);
		System.out.println("initilised GPIO pins");
		
		while (keepgoing) {
			switch (front) {
			case 0:
				IOfront.setState(PinState.HIGH);
				IOback.setState(PinState.LOW);
				//sets pins for not moving
				break;
			case 1:
				IOfront.setState(PinState.LOW);
				IOback.setState(PinState.LOW);
				//sets pins for 
				break;
			case 2:
				IOfront.setState(PinState.LOW);
				IOback.setState(PinState.HIGH);
				//sets pins for backwards
				break;
			}

			switch (side) {
			case 0:
				IOleft.setState(PinState.HIGH);
				IOright.setState(PinState.LOW);
				//sets pins for left
				break;
			case 1:
				IOleft.setState(PinState.LOW);
				IOright.setState(PinState.LOW);
				//sets pins for not turning
				break;
			case 2:
				IOleft.setState(PinState.LOW);
				IOright.setState(PinState.HIGH);
				//sets pins for right
				break;

			}
			
		}

		gpio.shutdown();

	}

}
