package com.example.pokedex.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.pokedex.data.PokemonListResult
import com.example.pokedex.extensions.getImageLoader
import com.example.pokedex.ui.theme.MdRed800
import com.example.pokedex.ui.theme.MdTeal500
import com.example.pokedex.ui.theme.White
import com.example.pokedex.viewmodel.PokemonViewModel
import kotlin.getValue
import androidx.compose.material3.Text as MaterialText

@Composable
fun Main() {
    val pokemonViewModel: PokemonViewModel = hiltViewModel()
    val pokemonList = pokemonViewModel.pokemonList.collectAsState().value
    val isLoading by pokemonViewModel::isLoading
    val listState = rememberLazyListState()

    pokemonViewModel.fetchPokemonList()

    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            CenterAlignedTopAppBar(
                colors = TopAppBarColors(
                    containerColor = MdRed800,
                    titleContentColor = White,
                    scrolledContainerColor = White,
                    navigationIconContentColor = White,
                    actionIconContentColor = White
                ),
                title = { MaterialText("Pokedex") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding() + 16.dp,
                    bottom = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                )
        ) {
            MaterialText(
                text = "Welcome to Pokedex!",
                modifier = Modifier.fillMaxWidth()
            )
            if (isLoading && pokemonList.isEmpty()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
            } else {
                SimpleList(
                    items = pokemonList,
                    onLoadMore = { pokemonViewModel.fetchPokemonList() },
                    listState = listState,
                    isLoading = isLoading
                )
                if (isLoading && pokemonList.isNotEmpty()) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@Composable
fun SimpleList(items: List<PokemonListResult>, onLoadMore: () -> Unit, listState: LazyListState, isLoading: Boolean = false) {
    LazyColumn(state = listState) {
        itemsIndexed(items) { index, item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 0.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = item.imageUrl,
                            imageLoader = getImageLoader()
                        ),
                        contentDescription = item.name,
                        modifier = Modifier.size(48.dp)
                    )
                    MaterialText(
                        text = item.name.replaceFirstChar { it.uppercase() },
                        color = MdTeal500,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
            if (index == items.lastIndex) {
                onLoadMore()
            }
        }
        if (isLoading) {
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}