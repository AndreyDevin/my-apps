package com.example.composablerickmorty.data
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.composablerickmorty.dto.entity.Item
import com.example.composablerickmorty.dto.entity.Response
import retrofit2.HttpException
import java.io.IOException
import kotlin.reflect.KSuspendFunction1

class MainPagingSource<R: Response, I: Item>(
    private val service: KSuspendFunction1<Int?, R>
): PagingSource<Int, I>()  {
    override fun getRefreshKey(state: PagingState<Int, I>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, I> {
        val position = params.key ?: 1

        return try {
            val response = service(position)

            LoadResult.Page(
                data = response.results as List<I>,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (response.results.isEmpty()) null else position + 1
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }

    fun create() = Pager(
        config = PagingConfig(pageSize = 40),
        pagingSourceFactory = { this }
    ).flow
}