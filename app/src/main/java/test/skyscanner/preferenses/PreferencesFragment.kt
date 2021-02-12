package test.skyscanner.preferenses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.serialization.ExperimentalSerializationApi
import org.koin.android.viewmodel.ext.android.viewModel
import test.skyscanner.R
import test.skyscanner.common.entities.Country
import test.skyscanner.common.entities.Currency
import test.skyscanner.common.mvi.MviView
import test.skyscanner.common.utils.clicks
import test.skyscanner.common.utils.itemClicks
import test.skyscanner.databinding.FragmentPreferencesBinding
import test.skyscanner.preferenses.PreferencesContract.CountryItemClickIntent
import test.skyscanner.preferenses.PreferencesContract.CurrencyItemClickIntent
import test.skyscanner.preferenses.PreferencesContract.ExitEffect
import test.skyscanner.preferenses.PreferencesContract.SaveClickIntent
import test.skyscanner.preferenses.PreferencesContract.State

@FlowPreview
@ExperimentalCoroutinesApi
@ExperimentalSerializationApi
class PreferencesFragment : DialogFragment(), MviView<State> {
    override val viewModel: PreferencesViewModel by viewModel()
    private lateinit var binding: FragmentPreferencesBinding

    init {
        init()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.AppTheme_NoActionBar)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        context?.let {
            dialog?.window?.statusBarColor = ContextCompat.getColor(it, R.color.colorPrimaryDark)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPreferencesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun intents(): Flow<Any> = merge(
        binding.btnSave.clicks().map { SaveClickIntent },
        binding.tilCountry.itemClicks().map { CountryItemClickIntent(it as Country) },
        binding.tilCurrency.itemClicks().map { CurrencyItemClickIntent(it as Currency) },
    )

    override fun render(state: State) {
        val atvCountries = (binding.tilCountry.editText as? AutoCompleteTextView)
        val countryAdapter = ArrayAdapter(requireContext(), R.layout.item_list, state.countries)
        atvCountries?.setText(state.selectedCountry)
        atvCountries?.setAdapter(countryAdapter)

        val atvCurrency = (binding.tilCurrency.editText as? AutoCompleteTextView)
        val currencyAdapter = ArrayAdapter(requireContext(), R.layout.item_list, state.currencies)
        atvCurrency?.setText(state.selectedCurrency)
        atvCurrency?.setAdapter(currencyAdapter)
    }

    override fun actions(action: Any) {
        when (action) {
            is ExitEffect -> this.dismiss()
        }
    }
}