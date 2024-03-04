package com.example.composablerickmorty.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.composablerickmorty.R
import com.example.composablerickmorty.dto.Character
import kotlinx.coroutines.flow.Flow

@Composable
fun CharacterItemView(
    character: Character,
    itemIndex: String,
    onClick: (Int) -> Unit,
    backPressed: () -> Unit
) {
    BackHandler { backPressed() }

    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(6.dp)
            .clickable { onClick(character.id) }
    ) {
        GlideImageWithPreview(
            data = character.image,
            modifier = Modifier.size(120.dp)
        )
        Column (modifier = Modifier.padding(start = 10.dp)){
            Text(text = "index -> $itemIndex")
            Text(text = character.name)
            Text(text = character.species)
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun GlideImageWithPreview(
    data: Any?,
    modifier: Modifier? = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Fit
) {
    if (data == null) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = contentDescription,
            modifier = modifier ?: Modifier,
            alignment = Alignment.Center,
            contentScale = contentScale
        )
    } else {
        GlideImage(
            model = data,
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier ?: Modifier
        )
    }
}

@Composable
fun CharactersList(
    pagingDataFlow: Flow<PagingData<Character>>,
    onItemClick: (Int) -> Unit,
    backPressed: () -> Unit
) {
    BackHandler { backPressed() }

    val characterPagingData: LazyPagingItems<Character> = pagingDataFlow.collectAsLazyPagingItems()

    //LazyColumn из коробки сохраняет позицию скролинга (фактически это индекс первого видимого итема)
    //но только пока мы не ушли с этой @Composable fun.
    //Eсли мы ушли на другой экран (другую @Composable fun),
    //а потом захотим вернуться назад, то функция перезапустится заново и позиция станет начальной.
    //Чтобы всегда возвращаться на позицию держу scrollState в синглтоне ScrollStateHolder
    val scrollState = ScrollStateHolder.scrollStateMap[ScrollStateHolder.CHARACTERS_LIST_KEY]

    LazyColumn(state = scrollState ?: LazyListState()) {
        items(
            // items требует обязательный параметр - число элементов, к которым возможен доступ
            // characterPagingData.itemCount увеличивается в процессе скролинга
            count = characterPagingData.itemCount
        ) { index ->
            characterPagingData[index]?.let {
                CharacterItemView(
                    character = it,
                    itemIndex = "$index(count=${characterPagingData.itemCount})",
                    onClick = onItemClick,
                    backPressed = backPressed
                )
            }
        }

        characterPagingData.apply {
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
                    val e = characterPagingData.loadState.refresh as LoadState.Error
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
                    val e = characterPagingData.loadState.refresh as LoadState.Error
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