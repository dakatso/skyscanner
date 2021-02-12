package test.skyscanner.quotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge
import kotlinx.serialization.ExperimentalSerializationApi
import org.koin.android.viewmodel.ext.android.viewModel
import test.skyscanner.R
import test.skyscanner.common.mvi.MviView
import test.skyscanner.common.utils.arguments
import test.skyscanner.databinding.FragmentQuotesBinding
import test.skyscanner.quotes.QuotesContract.State

@FlowPreview
@ExperimentalCoroutinesApi
@ExperimentalSerializationApi
class QuotesFragment : DialogFragment(), MviView<State> {
    override val viewModel by viewModel<QuotesViewModel> { arguments() }
    private lateinit var binding: FragmentQuotesBinding

    init {
        init()
    }

    private var adapter: QuotesAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentQuotesBinding.inflate(layoutInflater)
        return binding.root
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

        adapter = QuotesAdapter()

        binding.recycler.adapter = adapter
        binding.recycler.setHasFixedSize(true)
        binding.recycler.layoutManager = LinearLayoutManager(context)
        binding.recycler.addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
    }

    override fun intents(): Flow<Any> = merge(

    )

    override fun render(state: State) {
        adapter?.setItems(state.quotes)
        adapter?.notifyDataSetChanged()
    }

    override fun actions(action: Any) {
    }
}

