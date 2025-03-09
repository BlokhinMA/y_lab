package ru.ylab.server.services;

import ru.ylab.server.models.Transaction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class TransactionService {

    private final List<Transaction> transactions;

    public TransactionService(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void add(Transaction transaction) {
        transaction.setDateTime(LocalDateTime.now());
        transactions.add(transaction);
    }

    public List<Transaction> getByUserEmail(String userEmail) {
        List<Transaction> transactionsList = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (Objects.equals(transaction.getUserEmail(), userEmail))
                transactionsList.add(transaction);
        }
        return transactionsList;
    }

    public List<Transaction> getByUserEmailFilteredByDate(String userEmail, LocalDate date) {
        List<Transaction> transactionsList = getByUserEmail(userEmail);
        List<Transaction> filteredList = new ArrayList<>();
        for (Transaction transaction : transactionsList) {
            if (Objects.equals(transaction.getDateTime().toLocalDate(), date))
                filteredList.add(transaction);
        }
        return filteredList;
    }

    public List<Transaction> getByUserEmailFilteredByCategory(String userEmail, String category) {
        List<Transaction> transactionsList = getByUserEmail(userEmail);
        List<Transaction> filteredList = new ArrayList<>();
        for (Transaction transaction : transactionsList) {
            if (Objects.equals(transaction.getCategory(), category))
                filteredList.add(transaction);
        }
        return filteredList;
    }

    public List<Transaction> getByUserEmailFilteredByType(String userEmail, int numberOfType) {
        String type = "";
        if (numberOfType == 1)
            type = "Доход";
        if (numberOfType == 2)
            type = "Расход";
        List<Transaction> transactionsList = getByUserEmail(userEmail);
        List<Transaction> filteredList = new ArrayList<>();
        for (Transaction transaction : transactionsList) {
            if (Objects.equals(transaction.getType(), type))
                filteredList.add(transaction);
        }
        return filteredList;
    }

    public void deleteByIndexAndUserEmail(int index, String userEmail) {
        if (index < 0)
            return;
        List<Integer> indexes = getIndexes(userEmail);
        if (!indexes.isEmpty())
            transactions.remove(indexes.get(index - 1).intValue());
    }

    public void editByIndexAndUserEmail(int index, String userEmail, Transaction transaction) {
        if (index < 0)
            return;
        List<Integer> indexes = getIndexes(userEmail);
        if (!indexes.isEmpty()) {
            Transaction editableTransaction = transactions.get(indexes.get(index - 1));
            editableTransaction.setSum(transaction.getSum());
            editableTransaction.setCategory(transaction.getCategory());
            editableTransaction.setDescription(transaction.getDescription());
        }
    }

    private List<Integer> getIndexes(String userEmail) {
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < transactions.size(); i++)
            if (Objects.equals(transactions.get(i).getUserEmail(), userEmail))
                indexes.add(i);
        return indexes;
    }

}
