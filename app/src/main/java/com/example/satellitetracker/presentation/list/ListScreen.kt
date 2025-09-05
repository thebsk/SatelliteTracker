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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.satellitetracker.data.model.Satellite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    onSatelliteClick: (Int) -> Unit,
    viewModel: ListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Satellites") },
                actions = {
                    TextField(
                        value = uiState.searchQuery,
                        onValueChange = { query ->
                            viewModel.processIntent(ListIntent.SearchQueryChanged(query))
                        },
                        placeholder = { Text("Search...") },
                        modifier = Modifier.fillMaxWidth().padding(end = 16.dp)
                    )
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator()
                }
                uiState.errorMessage != null -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = uiState.errorMessage ?: "Unknown error")
                        Text(
                            text = "Tap to retry",
                            modifier = Modifier.clickable {
                                viewModel.processIntent(ListIntent.Refresh)
                            }
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

@Composable
fun SatelliteList(
    satellites: List<Satellite>,
    onSatelliteClick: (Int) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(satellites) { satellite ->
            SatelliteListItem(
                satellite = satellite,
                onClick = { onSatelliteClick(satellite.id) }
            )
            HorizontalDivider()
        }
    }
}

@Composable
fun SatelliteListItem(
    satellite: Satellite,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(if (satellite.active) Color.Green else Color.Red)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = satellite.name)
    }
}
