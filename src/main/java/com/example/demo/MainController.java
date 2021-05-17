package com.example.demo;

import com.example.demo.model.Question;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@Controller
public class MainController {
    private Connection connection;
    private String IS_IN_TABLE = "SELECT * FROM users WHERE name = ?";
    private String ADD_NEW_USER = "INSERT INTO user SET name = ?";
    private String GET_FIVE_RANDOM_QUESTIONS = "SELECT * FROM questions ORDER BY RAND() LIMIT 5";

    public void init() {
        try {
            connection  = new DataSourceBean(
                    "jdbc:mysql://127.0.0.1:3306/forth-module",
                    "root",
                    "",
                    "org.mariadb.jdbc.Driver"
            ).getDataSource().getConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @GetMapping("/authorize")
    public String authorizerUser(
            @RequestParam String userName) {
        init();
        if (!isInList(userName)) {
            addNewUser(userName);
        }

        return "mainPage.html";
    }

    private void addNewUser(String userName) {
        try (PreparedStatement statement = connection.prepareStatement(ADD_NEW_USER)) {
            statement.setString(1, userName);
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public boolean isInList(String userName) {
        try (PreparedStatement statement = connection.prepareStatement(IS_IN_TABLE)) {
            statement.setString(1, userName);
            statement.execute();
            if (statement.getResultSet().next()) {
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    @RequestMapping("/getFiveRandomQuestions")
    public String getFiveRandomQuestion() {
        List<Question> questions = new ArrayList<>();
        init();
        try (Statement statement = connection.createStatement()) {
            statement.execute(GET_FIVE_RANDOM_QUESTIONS);
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                questions.add(
                        new Question(
                                resultSet.getLong("id"),
                                resultSet.getString("question"),
                                resultSet.getString("answer"),
                                resultSet.getInt("points")
                        )
                );
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.out.println(questions);
        return "otherPage.html";
    }


}
