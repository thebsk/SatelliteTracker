package com.example.satellitetracker.presentation.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.satellitetracker.R
import com.example.satellitetracker.domain.model.Satellite
import kotlinx.coroutines.flow.onEach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    onSatelliteClick: (Int) -> Unit,
    showSnackBar: (String) -> Unit,
    viewModel: ListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState(initial = ListUiState())
    val context = LocalContext.current

    LaunchedEffect(key1 = viewModel) {
        with(viewModel) { setEvent(ListEvent.LoadSatellites) }
    }

    LaunchedEffect(key1 = viewModel.effect) {
        viewModel.effect.onEach { effect ->
            when (effect) {
                is ListEffect.ShowError -> {
                    showSnackBar(context.getString(R.string.error_prefix, effect.message))
                }
            }
        }.collect {}
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.satellites_title)) },
                actions = {}
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { query ->
                    with(viewModel) { setEvent(ListEvent.SearchQueryChanged(query)) }
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            with(viewModel) { setEvent(ListEvent.SearchQueryChanged("")) }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(id = R.string.content_description_clear)
                            )
                        }
                    }
                },
                placeholder = { Text(stringResource(id = R.string.search_placeholder)) },
                modifier = Modifier
                    .fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator()
                    }

                    uiState.errorMessage != null -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = uiState.errorMessage
                                    ?: stringResource(id = R.string.unknown_error)
                            )
                        }
                    }

                    else -> {
                        SatelliteList(
                            satellites = uiState.filteredSatellites,
                            onSatelliteClick = onSatelliteClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SatelliteList(
    satellites: List<Satellite>,
    onSatelliteClick: (Int) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        itemsIndexed(satellites) { index, satellite ->
            SatelliteListItem(
                satellite = satellite,
                onClick = { onSatelliteClick(satellite.id) }
            )
            if (index < satellites.lastIndex) {
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun SatelliteListItem(
    satellite: Satellite,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(if (satellite.isActive) Color(0xFF2E7D32) else Color(0xFFC62828))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = satellite.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.clickable(onClick = onClick)
            )
        }
    }
}
