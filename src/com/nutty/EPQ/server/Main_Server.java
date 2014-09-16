package com.nutty.EPQ.server;

import java.io.BufferedReader;

/*
 * This is the class that Will run when the program is run
 * @author Tom Hazell
 * (C) 2014 Tom Hazell
 */

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main_Server extends Thread {
	// make object for the Move class (interacts with GPIO pins)
	static Move M = new Move();
	// makes the socket and the byte to receive data done here so they can be
	// static
	static DatagramSocket serverSocket = null;
	static byte[] receiveData = new byte[4];
	static byte[] sendData = new byte[4];
	// entery point
	public static void main(String[] args) throws EOFException {
		// set up the veriable that the IP will be stored in as a string and a
		// InetAddress
		InetAddress addr = null;
		String listen_address = null;

		// write the copy right and instructions
		System.out.println("Copyright (C) 2014 Tom Hazell");
		System.out
				.println("This uses the Pi4j library to control the GPIO pins. It is licensed under the Apache License, Version 2.0. For a copy go to: http://www.apache.org/licenses/LICENSE-2.0");
		System.out.println("");
		System.out
				.println("Make sure the raspberry pi is connected to a network (WIFI or Ethernet) and use ifconfig in terminal to find the IP address of the raspberry pi.");
		System.out.println("Enter the IP for the raspberry pi to listen on.");
		System.out.print("IP:");
		// creates BuffredReader so input can be taken
		BufferedReader In = new BufferedReader(new InputStreamReader(System.in));

		try {
			// take console input so user can enter IP
			listen_address = In.readLine();
		} catch (IOException e1) {
			e1.printStackTrace();
			System.out.println("ERROR: failed to get input from console ("
					+ e1.toString() + ")");
			System.exit(1);
			// If getting input fails the program quits and displays an error
			// message
		}

		try {
			In.close();
			// close the BufferedReader used to get the input as to not use up
			// additinal resorces
			addr = InetAddress.getByName(listen_address);
			// uses the input given by the user and makes a InetAddress( basicly
			// an ip addres object)
			serverSocket = new DatagramSocket(40506, addr);
			// creates a DiagramSocket (a UDP socket) using the port 40506 and
			// the InetAddress addr

		} catch (UnknownHostException e) {
			System.out.println("Could not find host:" + listen_address);
			System.exit(1);
			e.printStackTrace();
			// Catch errors related to the use entering an invalid IP, and exits
			// the program if there is an error
		} catch (IOException e) {
			System.out.println("Could not listen on port: 40506.");
			System.exit(1);
			e.printStackTrace();
			// Catch errors related to being unable to lisen on the port at the
			// Specified address, and exits the program if there is an error
		}
		System.out.println("Connection Socket Created");
		// out put to user

		try {

			M.start();
			// starts the thread in the Move class

			boolean keepgoin = true;
			// while this is true the following will loop

			while (keepgoin) {
				int[] Arrayin = { 1, 0 };
				// creates the array to store the 2 ints that will be recived
				DatagramPacket receivePacket = new DatagramPacket(receiveData,
						receiveData.length);
				// creates a new diagram packet that the data recived will go in
				// to
				serverSocket.receive(receivePacket);
				// this looks for incoming packets and recives them to
				// recivepacket
				String one = new String(receivePacket.getData());
				int onei = Integer.parseInt(one.trim());
				// converts the packet to a string then trims it and converts it
				// to an int
				System.out.println(onei);
				if (onei == 6) {
					InetAddress sendAddress = receivePacket.getAddress();
					int port = receivePacket.getPort();
					int send = 1;
					
					sendData = Integer.toString(send).getBytes();
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, sendAddress, port);
					serverSocket.send(sendPacket);
					
				} else if (onei > 2) {
					Arrayin[1] = onei - 3;
					M.doit(Arrayin);
				} else {
					Arrayin[0] = onei;
					M.doit(Arrayin);
				}
				// becuase the 2 bits of data (front and side) are identical so
				// front/back is either 0,1 or 2 and side ways is 3, 4 or 5. so
				// this distingueshes whitch is whitch and and puts in its place
				// in the array
				/*
				 * serverSocket.receive(receivePacket); String two = new
				 * String(receivePacket.getData()); int twoi =
				 * Integer.parseInt(two.trim()); if (twoi > 2) { Arrayin[1] =
				 * twoi - 3;
				 * 
				 * } else { Arrayin[0] = twoi; } //dose the same again
				 */

				
				// runs the doit sub in move class this updates the values for
				// the move class
			}

		} catch (IOException e) {
			System.out.println("Accept failed." + e.toString());
			System.exit(1);
			// Catch errors
		} finally {

			serverSocket.close();
			// close server once everything has been done

		}

	}
}
