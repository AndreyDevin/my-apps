package com.example.composablerickmorty.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.composablerickmorty.dto.Location
import kotlinx.coroutines.flow.Flow

@Composable
fun LocationItemView(
    location: Location,
    itemIndex: String,
    ) {
    Column (modifier = Modifier.padding(10.dp)) {
        Text(text = "index -> $itemIndex")
        Text(text = "${location.name}  —  ${location.type}")
        Text(text = "dimension:  ${location.dimension}")
        Divider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = Color.DarkGray,
            thickness = 1.dp
        )
    }
}

@Composable
fun LocationsList(
    pagingDataFlow: Flow<PagingData<Location>>,
    backPressed: () -> Unit
) {
    BackHandler { backPressed() }

    val locationsPagingData: LazyPagingItems<Location> = pagingDataFlow.collectAsLazyPagingItems()

    val scrollState = ScrollStateHolder.scrollStateMap[ScrollStateHolder.LOCATIONS_LIST_KEY]

    LazyColumn (state = scrollState ?: LazyListState()) {
        items(
            // items требует обязательный параметр - число элементов, к которым возможен доступ
            // characterPagingData.itemCount увеличивается в процессе скролинга
            count = locationsPagingData.itemCount
        ) { index ->
            locationsPagingData[index]?.let {
                LocationItemView(
                    location = it,
                    itemIndex = "$index(count=${locationsPagingData.itemCount})"
                    )
            }
        }

        locationsPagingData.apply {
            when {
                //когда список в процессе полной перезагрузки, никаких элементов нет на экране
                loadState.refresh is LoadState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
                //есть загруженная страница, но идёт процесс подзагрузки следующей страницы
                loadState.append is LoadState.Loading -> {
                    item { CircularProgressIndicator() }
                }
                //состояние, когда не удалось загрузить ни одного элемента
                loadState.refresh is LoadState.Error -> {
                    val e = locationsPagingData.loadState.refresh as LoadState.Error
                    item {
                        Column(modifier = Modifier.fillParentMaxSize()) {
                            e.error.localizedMessage?.let { Text(text = it) }
                            Button(onClick = { retry() }) {
                                Text(text = "Retry")
                            }
                        }
                    }
                }
                //когда список удалось загрузить частично, в конце добавляем кнопку Retry
                loadState.append is LoadState.Error -> {
                    val e = locationsPagingData.loadState.refresh as LoadState.Error
                    item {
                        Column(modifier = Modifier.fillParentMaxSize()) {
                            e.error.localizedMessage?.let { Text(text = it) }
                            Button(onClick = { retry() }) {
                                Text(text = "Retry")
                            }
                        }
                    }
                }
            }
        }
    }
}