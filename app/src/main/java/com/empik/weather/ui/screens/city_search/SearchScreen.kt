package com.empik.weather.ui.screens.city_search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.empik.weather.R
import com.empik.weather.data.api.models.response.CityResponseItem
import org.koin.androidx.compose.getViewModel


@Composable
fun SearchScreen(
    onCitySelected: (CityResponseItem) -> Unit,
) {
    val viewModel = getViewModel<CitySearchViewModel>()

    val state by viewModel.state.collectAsStateWithLifecycle()

    state.citySelected?.let {
        viewModel.saveCity(it)
        onCitySelected(it)
    }

    SearchScreenContent(
        state = state,
        onCitySearchByQuery = { viewModel.getCitiesByQuery(it) },
        onCitySearch = { viewModel.getCityByName(it) },
        onCitySelected = {
            viewModel.saveCity(it)
            onCitySelected(it) },
        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreenContent(
    state: CitySearchScreenState,
    onCitySearchByQuery: (String) -> Unit,
    onCitySearch: (String) -> Unit,
    onCitySelected: (CityResponseItem) -> Unit,
) {
    var text by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    val savedCitiesFiltered = remember (text) {
        state.savedCities.filter { it.localizedName.contains(text, ignoreCase = true) }
    }

    Scaffold { contentPadding ->
        SearchBar(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxWidth(),
            query = text,
            onQueryChange = {
                text = it
                if (it.length > 2) {
                    onCitySearchByQuery(it)
                }
            },
            onSearch = {
                onCitySearch(it)
                active = false
            },
            active = active,
            onActiveChange = { active = it },
            placeholder = {
                Text(text = stringResource(R.string.search_hint))
            },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "search ico")
            },
            trailingIcon = {
                if (active) {
                    Icon(
                        modifier = Modifier.clickable {
                            if (text.isEmpty()) {
                                active = false
                            } else {
                                text = ""
                            }
                        },
                        imageVector = Icons.Default.Clear,
                        contentDescription = "clear ico"
                    )
                }
            },
        ) {
            LazyColumn {
                items(savedCitiesFiltered.size) { index ->
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = "saved icon", tint = Color.Red)
                        Text(
                            text = savedCitiesFiltered[index].localizedName,
                            modifier = Modifier
                                .clickable {
                                    active = false
                                    onCitySelected(savedCitiesFiltered[index])
                                }
                                .padding(8.dp),
                        )
                    }
                }

                items(state.fetchedCitiesNames.size) { index ->
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(imageVector = Icons.Default.Place, contentDescription = "geo icon")
                        Text(
                            text = state.fetchedCitiesNames[index].localizedName,
                            modifier = Modifier
                                .clickable {
                                    active = false
                                    onCitySelected(state.fetchedCitiesNames[index])
                                }
                                .padding(8.dp),
                        )
                    }
                }
            }

        }
    }
}

@Composable
@Preview
fun SearchScreenContentPreview() {
    val state = CitySearchScreenState()
    SearchScreenContent(state, {}, {}, {})
}