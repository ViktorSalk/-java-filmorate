package ru.yandex.practicum.filmorate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.Map;
import java.util.Scanner;

@SpringBootApplication
public class FilmorateApplication {
	public static void main(String[] args) {
		SpringApplication.run(FilmorateApplication.class, args);

		final Gson gson = new Gson();
		final Scanner scanner = new Scanner(System.in);
		System.out.print("Введите JSON => ");
		final String input = scanner.nextLine();
		try {
			gson.fromJson(input, Map.class);
			System.out.println("Был введён корректный JSON");
		} catch (JsonSyntaxException exception) {
			System.out.println("Был введён некорректный JSON");
		}
	}
}
