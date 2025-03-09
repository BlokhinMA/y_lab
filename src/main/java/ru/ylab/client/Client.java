package ru.ylab.client;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {

        try (Socket socket = new Socket("localhost", 8080)) {
            try (BufferedWriter writer =
                         new BufferedWriter(
                                 new OutputStreamWriter(
                                         socket.getOutputStream()));
                 BufferedReader reader =
                         new BufferedReader(
                                 new InputStreamReader(
                                         socket.getInputStream()));
                 Scanner scanner = new Scanner(System.in)) {

                while (true) {

                    String request;
                    String response;

                    while (!Objects.equals(request = reader.readLine(), "END")) {
                        System.out.println(request);
                    }

                    response = scanner.next();
                    writer.write(response);
                    writer.flush();

                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
