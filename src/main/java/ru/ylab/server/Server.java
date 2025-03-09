package ru.ylab.server;

import ru.ylab.server.models.Transaction;
import ru.ylab.server.models.User;
import ru.ylab.server.services.TransactionService;
import ru.ylab.server.services.UserService;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {

    public static void main(String[] args) {

        try (ServerSocket server = new ServerSocket(8080)) {

            System.out.println("Сервер запущен");

            List<User> users = new ArrayList<>();
            users.add(new User("root", "root@root", ")Rn=4W4=0vAgs/K", false,
                    "admin"));
            var userService = new UserService(users);

            try (Socket socket = server.accept();
                 BufferedWriter writer =
                         new BufferedWriter(
                                 new OutputStreamWriter(
                                         socket.getOutputStream()));
                 BufferedReader reader =
                         new BufferedReader(
                                 new InputStreamReader(
                                         socket.getInputStream()));
                 Scanner scanner = new Scanner(System.in)) {

                int value;

                while (true) {
                    writer.write("1. Регистрация\n");
                    writer.write("2. Авторизация\n");
                    writer.write("3. Выход\n");
                    writer.write("Введите значение: \n");
                    writer.write("END");
                    writer.flush();
                    /*System.out.println("1. Регистрация");
                    System.out.println("2. Авторизация");
                    System.out.println("3. Выход");
                    System.out.print("Введите значение: ");*/

                    //value = enterValue(3, scanner);

                    value = Integer.parseInt(reader.readLine());

                    writer.write("РЕГИСТРАЦИЯ\n");
                    writer.flush();

                    switch (value) {
                        case 1:
                            register(writer, scanner, userService);
                            next(scanner);
                            break;
                        case 2:
                            User loginUser = login(scanner, userService);
                            transactions(loginUser, userService, scanner);
                            break;
                        case 3:
                            scanner.close();
                            return;
                    }
                }

            } catch (NullPointerException e) {
                throw new RuntimeException(e);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void register(BufferedWriter writer, Scanner scanner, UserService userService) throws IOException {
//        System.out.println("РЕГИСТРАЦИЯ");

        writer.write("РЕГИСТРАЦИЯ\n");
        writer.flush();

        User newUser = new User();

        do {
            System.out.print("Введите имя пользователя: ");
            newUser.setName(scanner.next());
            System.out.print("Введите email пользователя: ");
            newUser.setEmail(scanner.next());
            System.out.print("Введите пароль пользователя: ");
            newUser.setPassword(scanner.next());

            if (userService.create(newUser))
                break;
            else System.out.println("Пользователь с таким email уже существует. Введите данные снова");

        } while (true);

        System.out.println("Вы успешно зарегистрировались!");
    }

    private static User login(Scanner scanner, UserService userService) {
        System.out.println("АВТОРИЗАЦИЯ");

        User loginUser = new User();

        do {
            System.out.print("Введите логин: ");
            loginUser.setEmail(scanner.next());
            System.out.print("Введите пароль: ");
            loginUser.setPassword(scanner.next());

            if (userService.login(loginUser))
                break;
            else System.out.println("Неверные данные. Попробуйте еще раз");

        } while (true);

        System.out.println("Вы успешно авторизовались!");
        return loginUser;
    }

    private static void transactions(User loginUser, UserService userService, Scanner scanner) {
        var transactionService = new TransactionService(new ArrayList<>());
        int value;
        boolean isLogin = true;
        while (isLogin) {
            System.out.println("1. Добавить транзакцию");
            System.out.println("2. Редактировать транзакцию");
            System.out.println("3. Удалить транзакцию");
            System.out.println("4. Просмотреть транзакции");
            System.out.println("5. Удалить пользователя");
            System.out.println("6. Выйти из системы");
            System.out.print("Введите значение: ");

            value = enterValue(6, scanner);

            switch (value) {
                case 1:
                    addTransaction(transactionService, loginUser.getEmail(), scanner);
                    next(scanner);
                    break;
                case 2:
                    editTransaction(transactionService, loginUser.getEmail(), scanner);
                    next(scanner);
                    break;
                case 3:
                    deleteTransaction(transactionService, loginUser.getEmail(), scanner);
                    next(scanner);
                    break;
                case 4:
                    showTransactions(transactionService, loginUser.getEmail(), scanner);
                    break;
                case 5:
                    if (deleteUser(loginUser, userService, scanner)) break;
                    loginUser.setEmail(null);
                    loginUser.setPassword(null);
                    isLogin = false;
                    break;
                case 6:
                    loginUser.setEmail(null);
                    loginUser.setPassword(null);
                    isLogin = false;
            }

        }
    }

    private static void addTransaction(TransactionService transactionService, String userEmail, Scanner scanner) {
        Transaction transaction = new Transaction();

        System.out.println("ТИП ТРАНЗАКЦИИ");
        System.out.println("1. Доход");
        System.out.println("2. Расход");
        System.out.print("Введите значение: ");

        int type = enterValue(2, scanner);

        if (type == 1) transaction.setType("Доход");
        if (type == 2) transaction.setType("Расход");

        enterSumCategoryDescription(transaction, scanner);

        transaction.setUserEmail(userEmail);

        transactionService.add(transaction);
    }

    private static void editTransaction(TransactionService transactionService, String userEmail, Scanner scanner) {
        System.out.println("РЕДАКТИРОВАНИЕ ТРАНЗАКЦИИ");

        List<Transaction> transactions = showAllTransactions(transactionService, userEmail);
        if (transactions.isEmpty()) return;

        System.out.print("Выберите, какую транзакцию Вы хотите редактировать: ");
        int index = enterValue(transactions.size(), scanner);
        Transaction transaction = new Transaction();
        enterSumCategoryDescription(transaction, scanner);
        transactionService.editByIndexAndUserEmail(index, userEmail, transaction);
    }

    private static void deleteTransaction(TransactionService transactionService, String userEmail, Scanner scanner) {
        System.out.println("УДАЛЕНИЕ ТРАНЗАКЦИИ");

        List<Transaction> transactions = showAllTransactions(transactionService, userEmail);
        if (transactions.isEmpty()) return;

        System.out.print("Выберите, какую транзакцию Вы хотите удалить: ");
        int index = enterValue(transactions.size(), scanner);
        transactionService.deleteByIndexAndUserEmail(index, userEmail);
    }

    private static void showTransactions(TransactionService transactionService, String userEmail, Scanner scanner) {
        System.out.println("ПРОСМОТРЕТЬ ТРАНЗАКЦИИ");

        boolean flag = true;
        int value;

        while (flag) {
            System.out.println("1. Просмотреть все транзакции");
            System.out.println("2. Просмотреть транзакции, отфильтрованные по дате");
            System.out.println("3. Просмотреть транзакции, отфильтрованные по категории");
            System.out.println("4. Просмотреть транзакции, отфильтрованные по типу");
            System.out.println("5. Выход");
            System.out.print("Введите значение: ");

            value = enterValue(5, scanner);

            switch (value) {
                case 1:
                    showAllTransactions(transactionService, userEmail);
                    next(scanner);
                    break;
                case 2:
                    showFilteredTransactionsByDate(transactionService, userEmail, scanner);
                    next(scanner);
                    break;
                case 3:
                    showFilteredTransactionsByCategory(transactionService, userEmail, scanner);
                    next(scanner);
                    break;
                case 4:
                    showFilteredTransactionsByType(transactionService, userEmail, scanner);
                    next(scanner);
                    break;
                case 5:
                    flag = false;
            }
        }
    }

    private static List<Transaction> showAllTransactions(TransactionService transactionService, String userEmail) {
        System.out.println("ВСЕ ТРАНЗАКЦИИ");
        List<Transaction> transactions = transactionService.getByUserEmail(userEmail);

        return printTransactions(transactions, "У Вас пока нет транзакций");
    }

    private static void showFilteredTransactionsByDate(TransactionService transactionService, String userEmail,
                                                       Scanner scanner) {
        System.out.println("ТРАНЗАКЦИИ, ОТФИЛЬТРОВАННЫЕ ПО ДАТЕ");

        String pattern = "dd.MM.yyyy";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDate date = null;
        boolean isValidDate = false;

        while (!isValidDate) {
            System.out.print("Введите дату в формате \"дд.мм.гггг\": ");
            String dateString = scanner.next();

            try {
                date = LocalDate.parse(dateString, formatter);
                isValidDate = true;
            } catch (DateTimeParseException e) {
                System.out.println("Введенное значение не соответствует формату даты. Попробуйте снова");
            }
        }

        List<Transaction> transactions = transactionService.getByUserEmailFilteredByDate(userEmail, date);

        printTransactions(transactions, "Транзакций с такой датой не существует");
    }

    private static void showFilteredTransactionsByCategory(TransactionService transactionService, String userEmail,
                                                           Scanner scanner) {
        System.out.println("ТРАНЗАКЦИИ, ОТФИЛЬТРОВАННЫЕ ПО КАТЕГОРИИ");

        System.out.print("Введите категорию: ");

        String category = scanner.next();

        List<Transaction> transactions = transactionService.getByUserEmailFilteredByCategory(userEmail, category);

        printTransactions(transactions, "Транзакций с такой категорией не существует");
    }

    private static void showFilteredTransactionsByType(TransactionService transactionService, String userEmail,
                                                       Scanner scanner) {
        System.out.println("ТРАНЗАКЦИИ, ОТФИЛЬТРОВАННЫЕ ПО ТИПУ");

        System.out.print("Введите тип (1 - доход, 2 - расход): ");

        int type = enterValue(2, scanner);

        List<Transaction> transactions = transactionService.getByUserEmailFilteredByType(userEmail, type);

        printTransactions(transactions, "Транзакций с таким типом не существует");
    }

    private static List<Transaction> printTransactions(List<Transaction> transactions, String message) {
        if (transactions.isEmpty()) {
            System.out.println(message);
            return transactions;
        }

        for (int i = 0; i < transactions.size(); i++) {
            System.out.println(i + 1 + ". " + transactions.get(i));
            System.out.println("------------------------------");
        }

        return transactions;
    }

    private static boolean deleteUser(User loginUser, UserService userService, Scanner scanner) {
        System.out.println("Вы уверены?");
        System.out.println("1. Да");
        System.out.println("2. Нет");
        System.out.print("Введите значение: ");
        int value = enterValue(2, scanner);
        if (value == 1) {
            userService.deleteByEmail(loginUser.getEmail());
            return false;
        } else return true;
    }

    private static void enterSumCategoryDescription(Transaction transaction, Scanner scanner) {
        System.out.println("СУММА");
        System.out.print("Введите значение: ");
        double sum = scanForDouble(scanner);
        transaction.setSum(sum);

        System.out.println("КАТЕГОРИЯ");
        System.out.print("Введите значение: ");
        String category = scanner.next();
        transaction.setCategory(category);

        System.out.println("ОПИСАНИЕ");
        System.out.print("Введите значение: ");
        String description = scanner.next();
        transaction.setDescription(description);
    }

    private static void next(Scanner scanner) {
        System.out.print("Для продолжения нажмите любую клавишу: ");
        scanner.next();
    }

    private static int enterValue(int maxValue, Scanner scanner) {
        int value;

        do {
            value = scan(scanner);
            if (value < 1 || value > maxValue)
                System.out.print("Ошибка, введите другое значение: ");
            else break;
        } while (true);

        return value;
    }

    private static double scanForDouble(Scanner scanner) {
        Double number = null;
        while (number == null) {
            if (scanner.hasNextDouble())
                number = scanner.nextDouble();
            else {
                System.out.print("Значение должно быть целым или дробным числом " +
                        "(дробная часть указывается через точку). Попробуйте еще раз: ");
                scanner.next();
            }
        }
        return number;
    }

    private static int scan(Scanner scanner) {
        Integer number = null;
        while (number == null) {
            if (scanner.hasNextInt())
                number = scanner.nextInt();
            else {
                System.out.print("Значение должно быть целым числом. Попробуйте еще раз: ");
                scanner.next();
            }
        }
        return number;
    }

}
