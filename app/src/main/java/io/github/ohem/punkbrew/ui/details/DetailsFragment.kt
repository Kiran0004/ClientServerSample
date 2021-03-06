package io.github.ohem.punkbrew.ui.details

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import io.github.ohem.punkbrew.R
import io.github.ohem.punkbrew.data.db.BeerEntity
import io.github.ohem.punkbrew.ui.base.BaseFragment
import io.github.ohem.punkbrew.ui.details.items.HeaderItem
import io.github.ohem.punkbrew.ui.details.items.TextItem
import io.github.ohem.punkbrew.ui.list.BeersView
import io.github.ohem.punkbrew.util.DateFormatter
import io.github.ohem.punkbrew.util.EMPTY_PLACEHOLDER
import io.github.ohem.punkbrew.util.hide
import io.github.ohem.punkbrew.util.show
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.content_details.*
import kotlinx.android.synthetic.main.fragment_details.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class DetailsFragment : BaseFragment<DetailsViewModel>() {

    override val viewModel: DetailsViewModel by viewModel()
    override val layoutId: Int = R.layout.fragment_details

    private val gson: Gson by inject()
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()
    private var beerId = -1
    private val disposables = ArrayList<Disposable>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            beerId = it.getInt(BEER_ID_KEY)
        }

        restoreSavedInstanceState(savedInstanceState)

        if (savedInstanceState == null) {
            viewModel.findBeer(beerId)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_details, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun setupToolbar() {
        details_toolbar.also {
            (activity as AppCompatActivity).setSupportActionBar(it)
            it.setNavigationIcon(R.drawable.ic_back)
            it.setNavigationOnClickListener { finish() }
        }
    }

    override fun initView() {
        beer_details_recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
            adapter = groupAdapter
        }
        val beerId = arguments?.getInt(BEER_ID_KEY) ?: -1
        if (beerId != -1) {
            viewModel.beer.observe(this, Observer { single ->
                disposables.add(
                    single.subscribeOn(Schedulers.io())
                        //.delay(5, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            { beer ->
                                Timber.d("Received beer: $beer")
                                viewModel.currentBeer = beer
                                run {
                                    initViews(beer)
                                    onContentReceived()
                                }
                            },
                            { e: Throwable ->
                                Timber.d(
                                    "Error occurred while receiving a beer with number %d, %s",
                                    beerId,
                                    e.message
                                )
                                onEmptyContent()
                            }
                        )
                )
            })
        }
    }
    private fun initViews(beer: BeerEntity) {
        details_toolbar.title = beer.name
        if (beer.imageUrl.isNullOrEmpty()) {
            beer_details_image.setImageResource(R.drawable.bottle)
        } else {
            Picasso.get()
                .load(beer.imageUrl)
                .error(R.drawable.bottle)
                .fit().centerInside()
                .into(
                    beer_details_image,
                    object : Callback {
                        override fun onSuccess() {
                            beer_details_image.alpha = 0f
                            beer_details_image.animate().setDuration(500).alpha(1f).start()
                        }

                        override fun onError(e: Exception?) {
                            Timber.d(
                                "Error occurred while loading image of beer with number %d, %s",
                                beerId,
                                e?.message ?: "Unknown error"
                            )
                        }
                    }
                )
        }
        beer_details_date.text = DateFormatter.formatDate(beer.firstBrewed, false)
        beer_details_name.text = beer.name
        beer_details_tagline.text = beer.tagline

        updateRecyclerView(beer)
    }


    private fun updateRecyclerView(beer: BeerEntity) {
        val descriptionSection = Section().apply {
            setHeader(HeaderItem(getString(R.string.header_description)))
            add(TextItem(beer.description ?: EMPTY_PLACEHOLDER))
        }

        val foodPairing: List<String> = gson.fromJson(
            beer.foodPairingJson,
            object : TypeToken<List<String>>() {}.type
        )
        val foodPairingSection = Section().apply {
            setHeader(HeaderItem(getString(R.string.header_food_pairing)))
            addAll(foodPairing.map {
                TextItem(
                    String.format(requireContext().getString(R.string.format_item_text_bullet), it)
                )
            })
        }

        val brewersTipsSection = Section().apply {
            setHeader(HeaderItem(getString(R.string.header_brewers_tips)))
            add(TextItem(beer.brewersTips))
        }

        groupAdapter.apply {
            add(descriptionSection)
            add(foodPairingSection)
            add(brewersTipsSection)
        }
    }

    override fun onDestroyView() {
        disposables.forEach { it.dispose(); }
        super.onDestroyView()
    }

    override fun onLoading() {
        details_progress_bar.show()
        details_content.hide()
        details_error.hide()
    }

    override fun onContentReceived() {
        details_content.show()
        details_error.hide()
        details_progress_bar.hide()
    }

    override fun onEmptyContent() {
        details_error.show()
        details_content.hide()
        details_progress_bar.hide()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(BEER_ID_KEY, beerId)
        super.onSaveInstanceState(outState)
    }

    private fun restoreSavedInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            beerId = savedInstanceState.getInt(BEER_ID_KEY, -1)
        }
    }

    private fun finish() {
        activity?.supportFragmentManager?.popBackStack()
    }

    companion object {

        private const val BEER_ID_KEY = "beer_id"

        @JvmStatic
        fun newInstance(id: Int): DetailsFragment {
            return DetailsFragment().apply {
                arguments = Bundle().apply {
                    putInt(BEER_ID_KEY, id)
                }
            }
        }
    }
}
