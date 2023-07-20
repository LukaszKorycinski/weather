package com.empik.weather.ui.screens.city_weather

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.empik.weather.R
import com.empik.weather.data.api.models.response.DailyForecast
import com.empik.weather.ui.theme.BlueSkyTop
import com.empik.weather.ui.theme.BueSkyBottom
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import kotlin.math.abs

@Composable
fun CityWeatherScreen(locationKey: String, cityName: String, onBack: () -> Unit) {
    val viewModel = getViewModel<CityWeatherViewModel>(parameters = { parametersOf(locationKey) })

    val state by viewModel.state.collectAsStateWithLifecycle()

    CityWeatherScreenContent(state, cityName, onBack)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CityWeatherScreenContent(state: CityWeatherState, cityName: String, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            CityWeatherTopBar(cityName, onBack)
        }
    ) { contentPadding ->
        ConstraintLayout (
            Modifier
                .padding(contentPadding)
                .fillMaxSize()
        ){
            val (pagerRef, previousRef, nextRef) = createRefs()

            val pagerState = rememberPagerState(initialPage = 0)

            HorizontalPager(
                modifier = Modifier
                    .constrainAs(pagerRef){
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(previousRef.top)
                    },
                pageCount = state.forecast?.dailyForecasts?.size ?: 0,
                pageSpacing = 16.dp,
                contentPadding = PaddingValues(start = 32.dp, end = 32.dp),
                state = pagerState
            ){ pageIndex ->
                val item = state.forecast?.dailyForecasts?.get(pageIndex)
                val pagerOffset by animateFloatAsState( (pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction,
                    label = "OffsetAnimation"
                )
                DayPage(pagerOffset, item, state, pageIndex)
            }

            val composableScope  = rememberCoroutineScope()
            Icon(
                modifier = Modifier
                    .constrainAs(previousRef) {
                        top.linkTo(pagerRef.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(nextRef.start)
                        bottom.linkTo(parent.bottom)
                    }
                    .size(48.dp)
                    .clickable {
                        composableScope.launch {
                            pagerState.animateScrollToPage(page = pagerState.currentPage - 1)
                        }
                    },
                tint = if (pagerState.currentPage == 0) Color.LightGray else Color.DarkGray,
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "previous day button")

            Icon(
                modifier = Modifier
                    .constrainAs(nextRef) {
                        top.linkTo(pagerRef.bottom)
                        start.linkTo(previousRef.end)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
                    .size(48.dp)
                    .clickable {
                        composableScope.launch {
                            pagerState.animateScrollToPage(page = pagerState.currentPage + 1)
                        }
                    },
                tint = if ( pagerState.currentPage == ((state.forecast?.dailyForecasts?.size ?: 0)-1) ) Color.LightGray else Color.DarkGray,
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "next day button")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityWeatherTopBar(cityName: String, onBack: () -> Unit) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ){
        IconButton(
            modifier = Modifier.fillMaxHeight(),
            onClick = { onBack() }) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(end = 56.dp)
                .align(Alignment.CenterVertically),
            textAlign = TextAlign.Center,
            maxLines = 1,
            text = cityName)
    }
}

@Composable
@OptIn(ExperimentalAnimationGraphicsApi::class)
private fun DayPage(
    pagerOffset: Float,
    item: DailyForecast?,
    state: CityWeatherState,
    pageIndex: Int,
) {
    ConstraintLayout(modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(0.75f)
        .graphicsLayer {
            scaleX = (1f - 0.25f * abs(pagerOffset))
            scaleY = (1f - 0.25f * abs(pagerOffset))
            rotationZ = 15f * pagerOffset
        }
        .clip(RoundedCornerShape(16.dp))
        .background(
            brush = Brush.verticalGradient(
                colors = listOf(BlueSkyTop, BueSkyBottom)
            )
        )
    ) {
        val (dayOfWeekRef, imgRef, temperatureRef, descriptionRef) = createRefs()
        translatedDayOfWeek(item?.getDayOfWeek())?.let { dayOfWeek ->
            Text(
                text = stringResource(dayOfWeek),
                modifier = Modifier
                    .padding(8.dp)
                    .constrainAs(dayOfWeekRef) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(imgRef.top)
                    },
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
            )
        }

        val image = AnimatedImageVector.animatedVectorResource(getWeatherIcon(item?.day?.icon ?: 0))
        var atEnd by remember { mutableStateOf(false) }
        LaunchedEffect(image) { atEnd = !atEnd }

        val painter = rememberAnimatedVectorPainter(animatedImageVector = image, atEnd = atEnd)
        Image(
            modifier = Modifier
                .size(160.dp)
                .padding(start = 8.dp, end = 8.dp)
                .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                .drawWithContent {
                    val colors = listOf(
                        Color.Black, Color.Black, Color.Black,
                        Color.Transparent
                    )
                    drawContent()
                    drawRect(
                        brush = Brush.verticalGradient(colors),
                        blendMode = BlendMode.DstIn
                    )
                }
                .constrainAs(imgRef) {
                    top.linkTo(dayOfWeekRef.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(temperatureRef.top)
                },
            painter = painter,
            contentDescription = "animated weather icon",
        )
        state.forecast?.dailyForecasts?.get(pageIndex)?.temperature?.maximum?.getCelsius()
            ?.let { temperature ->
                Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .constrainAs(temperatureRef) {
                            top.linkTo(imgRef.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(descriptionRef.top)
                        },
                    text = stringResource(R.string.degrees_celsius, temperature),
                    style = MaterialTheme.typography.headlineLarge,
                    color = when (temperature) {
                        in Double.NEGATIVE_INFINITY..10.0 -> Color.Blue
                        in 10.0..20.0 -> Color.Black
                        in 20.0..Double.POSITIVE_INFINITY -> Color.Red
                        else -> Color.White
                    }
                )
            }
        state.forecast?.dailyForecasts?.get(pageIndex)?.day?.iconPhrase?.let { weatherDescription ->
            Text(
                modifier = Modifier
                    .padding(8.dp)
                    .constrainAs(descriptionRef) {
                        top.linkTo(temperatureRef.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    },
                text = weatherDescription,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
        }
    }
}

fun getWeatherIcon(iconApi: Int): Int = when(iconApi){
    in 1 .. 2, 30 -> R.drawable.animated_sunny_day
    in 3 .. 6, 20, 21, 31, 32 -> R.drawable.animated_cloudy_sunny_day
    in 7 .. 11, 19 -> R.drawable.animated_cloudy_day
    in 12 .. 18 -> R.drawable.animated_rainy_day
    in 22 .. 29 -> R.drawable.animated_rainy_day
    else -> R.drawable.animated_sunny_day
}

fun translatedDayOfWeek(dayOfWeek: String?): Int? = when(dayOfWeek){
        "Monday" -> R.string.monday
        "Tuesday" -> R.string.tuesday
        "Wednesday" -> R.string.wednesday
        "Thursday" -> R.string.thursday
        "Friday" -> R.string.friday
        "Saturday" -> R.string.saturday
        "Sunday" -> R.string.sunday
        else -> null
    }


@Composable
@Preview
fun CityWeatherScreenContentPreview() {
//    CityWeatherScreenContent()
}