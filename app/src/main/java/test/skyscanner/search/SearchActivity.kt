package test.skyscanner.search

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.serialization.ExperimentalSerializationApi
import org.koin.android.viewmodel.ext.android.viewModel
import test.skyscanner.R
import test.skyscanner.common.entities.Place
import test.skyscanner.common.mvi.MviView
import test.skyscanner.common.utils.clicks
import test.skyscanner.common.utils.dateClicks
import test.skyscanner.common.utils.itemClicks
import test.skyscanner.common.utils.menuItemClicks
import test.skyscanner.common.utils.textChanges
import test.skyscanner.databinding.ActivitySearchBinding
import test.skyscanner.preferenses.PreferencesFragment
import test.skyscanner.quotes.QuotesFragment
import test.skyscanner.search.SearchContract.CalendarDateClickIntent
import test.skyscanner.search.SearchContract.FromItemClickIntent
import test.skyscanner.search.SearchContract.FromTextChangesIntent
import test.skyscanner.search.SearchContract.HideKeyboardEffect
import test.skyscanner.search.SearchContract.SearchClickIntent
import test.skyscanner.search.SearchContract.ShowEmptyPlacesDialogEffect
import test.skyscanner.search.SearchContract.ShowPreferencesScreenEffect
import test.skyscanner.search.SearchContract.ShowQuotesEffect
import test.skyscanner.search.SearchContract.State
import test.skyscanner.search.SearchContract.ToItemClickIntent
import test.skyscanner.search.SearchContract.ToTextChangesIntent
import test.skyscanner.search.SearchContract.ToolbarItemClickIntent
import java.util.*


@FlowPreview
@ExperimentalCoroutinesApi
@ExperimentalSerializationApi
class SearchActivity : AppCompatActivity(R.layout.activity_search), MviView<State> {
    override val viewModel: SearchViewModel by viewModel()
    private lateinit var binding: ActivitySearchBinding

    init {
        this.init()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.inflateMenu(R.menu.menu_search)
        val adapter = ArrayAdapter<Place>(this, android.R.layout.simple_dropdown_item_1line, listOf())

        with(binding.tilFrom.editText as AutoCompleteTextView) {
            threshold = 1
            setAdapter(adapter)
        }

        with(binding.tilTo.editText as AutoCompleteTextView) {
            threshold = 1
            setAdapter(adapter)
        }

        binding.calendar.minDate = Date().time
    }

    override fun intents(): Flow<Any> = merge(
        binding.toolbar.menuItemClicks().map(::ToolbarItemClickIntent),
        binding.tilFrom.editText?.textChanges()?.map(::FromTextChangesIntent) ?: emptyFlow(),
        binding.tilTo.editText?.textChanges()?.map(::ToTextChangesIntent) ?: emptyFlow(),
        (binding.tilTo.editText as? AutoCompleteTextView)?.itemClicks()?.map { FromItemClickIntent(it as Place) } ?: emptyFlow(),
        (binding.tilFrom.editText as? AutoCompleteTextView)?.itemClicks()?.map { ToItemClickIntent(it as Place) } ?: emptyFlow(),
        binding.calendar.dateClicks().onStart { emit(binding.calendar.date) }.map(::CalendarDateClickIntent),
        binding.btnSearch.clicks().map { SearchClickIntent },
    )

    override fun render(state: State) {
        updateSuggest(binding.tilFrom, state.fromPlaces)
        updateSuggest(binding.tilTo, state.toPlaces)
    }

    private fun updateSuggest(til: TextInputLayout, data: List<Place>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, data)
        (til.editText as AutoCompleteTextView).setAdapter(adapter)
        adapter.notifyDataSetChanged()
    }

    override fun actions(action: Any) {
        when (action) {
            is ShowPreferencesScreenEffect -> {
                PreferencesFragment().show(supportFragmentManager, null)
            }

            is ShowQuotesEffect -> {
                QuotesFragment()
                    .apply { arguments = bundleOf(ARGUMENTS_QUOTES to action.data) }
                    .show(supportFragmentManager, null)
            }

            is HideKeyboardEffect -> {
                currentFocus?.let {
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(it.windowToken, 0)
                }
            }

            is ShowEmptyPlacesDialogEffect -> {
                MaterialAlertDialogBuilder(this)
                    .setMessage(R.string.quotes_dialog_message)
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
            }
        }
    }

    companion object {
        const val ARGUMENTS_QUOTES = "argements_quotes"
    }
}
