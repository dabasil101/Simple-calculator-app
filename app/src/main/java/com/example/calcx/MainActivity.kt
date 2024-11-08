package com.example.calcx

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private val calculationHistory = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val end: ImageView = findViewById(R.id.end)
        end.setOnClickListener { finish() }

        val how: ImageView = findViewById(R.id.how)
        how.setOnClickListener {
            startActivity(Intent(applicationContext, How::class.java))
        }

        val currentEditText = findViewById<EditText>(R.id.current)

        // Add a clear button
        val clearButton = findViewById<Button>(R.id.ac)
        clearButton.setOnClickListener {
            currentEditText.text.clear() // Clears the current calculation area
        }

        // Array of number buttons for setting listeners in a loop
        val numberButtons = arrayOf(
            R.id.one to "1", R.id.two to "2", R.id.three to "3", R.id.four to "4", R.id.five to "5",
            R.id.six to "6", R.id.seven to "7", R.id.eight to "8", R.id.nine to "9", R.id.zerow to "0"
        )

        numberButtons.forEach { (id, text) ->
            findViewById<Button>(id).setOnClickListener {
                currentEditText.append(text)
            }
        }

        // Set listeners for operator buttons
        findViewById<Button>(R.id.add).setOnClickListener { currentEditText.append("+") }
        findViewById<Button>(R.id.sub).setOnClickListener { currentEditText.append("-") }
        findViewById<Button>(R.id.dev).setOnClickListener { currentEditText.append("/") }
        findViewById<Button>(R.id.mult).setOnClickListener { currentEditText.append("*") }

        // Add other operators as needed (e.g., multiply, divide)

        // Equal button listener to evaluate expression and update history
        findViewById<Button>(R.id.ans).setOnClickListener {
            val expression = currentEditText.text.toString()
            val result = try {
                evaluateAndUpdateHistory(expression)
            } catch (e: Exception) {
                "Error"
            }
            currentEditText.setText(result.toString())
        }
    }

    private fun evaluateAndUpdateHistory(expression: String): Double {
        val result = evaluateExpression(expression)
        calculationHistory.append(expression).append(" = ").append(result).append("\n")
        findViewById<EditText>(R.id.history1).setText(calculationHistory.toString())
        return result
    }

    private fun evaluateExpression(expression: String): Double {
        val numbers = mutableListOf<Double>()
        val operators = mutableListOf<Char>()
        var currentNumber = ""

        for (char in expression) {
            when {
                char.isDigit() || char == '.' -> currentNumber += char
                char == '+' || char == '-' || char == '*' || char == '/' -> {
                    if (currentNumber.isNotEmpty()) {
                        numbers.add(currentNumber.toDouble())
                        currentNumber = ""
                    }
                    while (operators.isNotEmpty() && hasPrecedence(char, operators.last())) {
                        val result = applyOperator(numbers.removeLast(), numbers.removeLast(), operators.removeLast())
                        numbers.add(result)
                    }
                    operators.add(char)
                }
            }
        }

        if (currentNumber.isNotEmpty()) {
            numbers.add(currentNumber.toDouble())
        }

        while (operators.isNotEmpty()) {
            val result = applyOperator(numbers.removeLast(), numbers.removeLast(), operators.removeLast())
            numbers.add(result)
        }

        return numbers.last()
    }

    private fun hasPrecedence(currentOp: Char, lastOp: Char): Boolean {
        return (currentOp == '+' || currentOp == '-') && (lastOp == '*' || lastOp == '/')
    }

    private fun applyOperator(second: Double, first: Double, operator: Char): Double {
        return when (operator) {
            '+' -> first + second
            '-' -> first - second
            '*' -> first * second
            '/' -> if (second != 0.0) first / second else throw IllegalArgumentException("Division by zero")
            else -> throw IllegalArgumentException("Unsupported operator")
        }
    }
}
