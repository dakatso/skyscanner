package test.skyscanner.common.utils

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.CalendarView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import org.koin.core.parameter.parametersOf
import java.util.*

@ExperimentalCoroutinesApi
fun View.clicks(): Flow<View> {
    return callbackFlow {
        setOnClickListener { offer(it) }
        awaitClose { setOnClickListener(null) }
    }
}

@ExperimentalCoroutinesApi
fun TextInputLayout.itemClicks(): Flow<Any> {
    return callbackFlow {
        val autoCompleteTextView = editText as AutoCompleteTextView
        autoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            offer(parent.getItemAtPosition(position))
        }
        awaitClose { autoCompleteTextView.onItemClickListener = null }
    }
}

@ExperimentalCoroutinesApi
fun Toolbar.menuItemClicks(): Flow<MenuItem> {
    return callbackFlow {
        setOnMenuItemClickListener { offer(it) }
        awaitClose { setOnMenuItemClickListener(null) }
    }
}

@ExperimentalCoroutinesApi
fun TextView.textChanges(): Flow<CharSequence> = callbackFlow<CharSequence> {
    val listener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            offer(s)
        }

        override fun afterTextChanged(s: Editable) = Unit
    }

    addTextChangedListener(listener)
    awaitClose { removeTextChangedListener(listener) }
}
    .conflate()

@ExperimentalCoroutinesApi
fun AutoCompleteTextView.itemClicks(): Flow<Any> = callbackFlow {
    val listener = AdapterView.OnItemClickListener { parent, _, position, _ ->
        offer(parent.getItemAtPosition(position))
    }
    onItemClickListener = listener
    awaitClose { onItemClickListener = null }
}.conflate()

@ExperimentalCoroutinesApi
fun CalendarView.dateClicks(): Flow<Long> = callbackFlow {
    setOnDateChangeListener { _, year, month, day ->
        val cal: Calendar = Calendar.getInstance()
        cal.set(year, month, day)
        offer(cal.timeInMillis)
    }
    awaitClose { setOnDateChangeListener(null) }
}.conflate()

fun Fragment.arguments() = parametersOf(arguments ?: Bundle.EMPTY)
