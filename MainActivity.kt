package com.example.calculadora2

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.calculadora2.databinding.ActivityMainBinding
import com.example.calculadora2.databinding.SecondLayoutBinding
import kotlin.math.cbrt
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

class MainActivity : AppCompatActivity() {

    private lateinit var basicBinding: ActivityMainBinding
    private lateinit var scientificBinding: SecondLayoutBinding
    private lateinit var inputTextView: TextView
    private lateinit var resultTextView: TextView
    private val currentInput = StringBuilder()
    private var isRadians: Boolean = true // Modo por defecto: RAD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Detectar orientación y seleccionar el diseño adecuado
        if (resources.configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) {
            basicBinding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(basicBinding.root)
            inputTextView = basicBinding.inputText
            resultTextView = basicBinding.resultText
            setupBasicCalculator()
        } else {
            scientificBinding = SecondLayoutBinding.inflate(layoutInflater)
            setContentView(scientificBinding.root)
            inputTextView = scientificBinding.inputText
            resultTextView = scientificBinding.resultText
            setupScientificCalculator()
        }
    }

    private fun setupBasicCalculator() {

        val buttonClickListener = View.OnClickListener { view ->
            val button = view as Button
            currentInput.append(button.text)
            inputTextView.text = currentInput.toString()
        }

        basicBinding.apply {
            listOf(
                btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9,
                buttonDot, buttonAdd,buttonSubtract, buttonMultiply, buttonDivide,buttonDelete
            ).forEach { it.setOnClickListener(buttonClickListener) }

            buttonClear.setOnClickListener { clearInput() }
            buttonEquals.setOnClickListener { evaluateAndDisplay() }
            buttonAddSub.setOnClickListener { toggleSign() }
            buttonParenthesis.setOnClickListener { toggleParenthesis()
            }
        }
        val buttonDelete = findViewById<Button>(R.id.buttonDelete)
        val display = findViewById<TextView>(R.id.inputText) // TextView donde se muestra el número

        buttonDelete.setOnClickListener {
            val currentText = display.text.toString()
            if (currentText.isNotEmpty()) {
                // Borra el último carácter del texto actual
                display.text = currentText.dropLast(1)
            }
        }
    }

    private fun setupScientificCalculator() {
        val buttonDelete = findViewById<Button>(R.id.buttonDelete)
        val display = findViewById<TextView>(R.id.inputText) // TextView donde se muestra el número

        buttonDelete.setOnClickListener {
            val currentText = display.text.toString()
            if (currentText.isNotEmpty()) {
                // Borra el último carácter del texto actual
                display.text = currentText.dropLast(1)
            }
        }
        val buttonClickListener = View.OnClickListener { view ->
            val button = view as Button
            currentInput.append(button.text)
            inputTextView.text = currentInput.toString()
        }

        scientificBinding.apply {
            // Vincular botones numéricos en el diseño horizontal
            listOf(
                btn0Land, btn1Land, btn2Land, btn3Land, btn4Land, btn5Land, btn6Land, btn7Land, btn8Land, btn9Land,
                btnDotLand, btnPlusLand, btnMinusLand, btnMultiplyLand, btnDivideLand
            ).forEach { it.setOnClickListener(buttonClickListener) }
            buttonClearLand.setOnClickListener { clearInput() }
            buttonEqualsLand.setOnClickListener { evaluateAndDisplay() }
            btnPercentLand.setOnClickListener { applyPercent() }
            btnParenthesisLand.setOnClickListener { toggleParenthesis() }
            // Vincular botones científicos
            listOf(
                buttonSinLand, buttonCosLand, buttonTanLand, buttonPiLand, buttonSquareLand,
                buttonSquareRootLand, buttonCubeRootLand, buttonExponentLand
            ).forEach { it.setOnClickListener(buttonClickListener) }

            // Funciones avanzadas
            buttonSinLand.setOnClickListener { applyTrigFunction(::sin) }
            buttonCosLand.setOnClickListener { applyTrigFunction(::cos) }
            buttonTanLand.setOnClickListener { applyTrigFunction(::tan) }
            buttonPiLand.setOnClickListener { appendToInput("3.1416") }
            buttonSquareLand.setOnClickListener { applySquare() }
            buttonSquareRootLand.setOnClickListener { applySquareRoot() }
            buttonCubeRootLand.setOnClickListener { applyCubeRoot() }
            buttonExponentLand.setOnClickListener { appendToInput("2.7183") }

            buttonRadLand.setOnClickListener {
                isRadians = !isRadians
                buttonRadLand.text = if (isRadians) "RAD" else "DEG"
            }

            buttonClearLand.setOnClickListener { clearInput() }
            buttonEqualsLand.setOnClickListener { evaluateAndDisplay() }
        }
    }


    private fun toggleSign() {
        val input = currentInput.toString()
        if (input.isNotEmpty()) {
            if (input.startsWith("-")) {
                currentInput.deleteCharAt(0)
            } else {
                currentInput.insert(0, "-")
            }
            inputTextView.text = currentInput.toString()
        }
    }

    private fun toggleParenthesis() {
        val input = currentInput.toString()
        val openCount = input.count { it == '(' }
        val closeCount = input.count { it == ')' }

        if (openCount > closeCount) {
            currentInput.append(")")
        } else {
            currentInput.append("(")
        }
        inputTextView.text = currentInput.toString()
    }

    private fun applyPercent() {
        val input = currentInput.toString().toDoubleOrNull() ?: 0.0
        updateResult(input / 100)
    }

    private fun evaluateAndDisplay() {
        val result = evaluate(currentInput.toString())
        updateResult(result)
    }

    private fun clearInput() {
        currentInput.clear()
        inputTextView.text = ""
        resultTextView.text = "0"
    }

    @SuppressLint("SetTextI18n")
    private fun updateResult(result: Double) {
        val formattedResult = if (result % 1 == 0.0) result.toInt().toString() else result.toString()
        resultTextView.text = formattedResult
        currentInput.clear()
        currentInput.append(formattedResult)
    }

    private fun evaluate(expression: String): Double {
        if (expression.isEmpty()) return 0.0

        if (expression.contains("(")) {
            val lastOpen = expression.lastIndexOf('(')
            val firstClose = expression.indexOf(')', lastOpen)
            if (lastOpen == -1 || firstClose == -1) throw IllegalArgumentException("Paréntesis no balanceados")
            val innerResult = evaluate(expression.substring(lastOpen + 1, firstClose))
            val newExpression = expression.substring(0, lastOpen) +
                    innerResult +
                    expression.substring(firstClose + 1)
            return evaluate(newExpression)
        }

        val multiplyDivideRegex = Regex("(-?\\d+(\\.\\d+)?)([*/])(-?\\d+(\\.\\d+)?)")
        multiplyDivideRegex.find(expression)?.let {
            val (left, _, operator, right) = it.destructured
            val result = if (operator == "*") left.toDouble() * right.toDouble() else left.toDouble() / right.toDouble()
            return evaluate(expression.replace(it.value, result.toString()))
        }

        val addSubtractRegex = Regex("(-?\\d+(\\.\\d+)?)([+-])(-?\\d+(\\.\\d+)?)")
        addSubtractRegex.find(expression)?.let {
            val (left, _, operator, right) = it.destructured
            val result = if (operator == "+") left.toDouble() + right.toDouble() else left.toDouble() - right.toDouble()
            return evaluate(expression.replace(it.value, result.toString()))
        }

        return expression.toDouble()
    }

    private fun applyTrigFunction(func: (Double) -> Double) {
        val input = currentInput.toString().toDoubleOrNull() ?: 0.0
        val value = if (isRadians) input else Math.toRadians(input)
        updateResult(func(value))
    }

    private fun applySquare() {
        val input = currentInput.toString().toDoubleOrNull() ?: 0.0
        updateResult(input.pow(2))
    }

    private fun applySquareRoot() {
        val input = currentInput.toString().toDoubleOrNull() ?: 0.0
        updateResult(sqrt(input))
    }

    private fun applyCubeRoot() {
        val input = currentInput.toString().toDoubleOrNull() ?: 0.0
        updateResult(cbrt(input))
    }

    private fun appendToInput(value: String) {
        currentInput.append(value)
        inputTextView.text = currentInput.toString()
    }
}

