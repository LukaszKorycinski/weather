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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.empik.weather.R
import com.empik.weather.data.api.models.response.DailyForecast
import com.empik.weather.data.api.models.response.Day
import com.empik.weather.data.api.models.response.ForecastResponse
import com.empik.weather.data.api.models.response.Headline
import com.empik.weather.data.api.models.response.Temperature
import com.empik.weather.data.api.models.response.TemperatureValue
import com.empik.weather.ui.TestTags
import com.empik.weather.ui.theme.BlueSkyTop
import com.empik.weather.ui.theme.BueSkyBottom
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun CityWeatherScreen(locationKey: String, cityName: String, onBack: () -> Unit) {
    val viewModel = getViewModel<CityWeatherViewModel>(parameters = { parametersOf(locationKey) })

    val state by viewModel.state.collectAsStateWithLifecycle()

    CityWeatherScreenContainer(state, cityName, onBack, onRetry = { viewModel.getForecast() })
}

@Composable
fun CityWeatherScreenContainer(state: CityWeatherState, cityName: String, onBack: () -> Unit, onRetry: () -> Unit) {
    Scaffold(
        topBar = {
            CityWeatherTopBar(cityName, onBack)
        }
    ) { contentPadding ->
        when (state){
            is CityWeatherState.ERROR -> ErrorScreen(state, contentPadding, onRetry)
            is CityWeatherState.CONTENT -> CityWeatherScreenContent(state, contentPadding, onBack)
            is CityWeatherState.LOADING -> LoadingScreen(contentPadding)
        }
    }
}

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun LoadingScreen(contentPadding: PaddingValues){
    Box(modifier = Modifier
            .padding(contentPadding)
            .fillMaxSize()
            .testTag(TestTags.LOADING_SCREEN),
        contentAlignment = Alignment.Center
        ){
        val image = AnimatedImageVector.animatedVectorResource(getWeatherIcon(R.drawable.animated_sunny_day))
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
                },
            painter = painter,
            contentDescription = "animated loading icon",
        )
    }
}

@Composable
fun ErrorScreen(error: CityWeatherState.ERROR, contentPadding: PaddingValues, onRetry: () -> Unit){
    Column(
        modifier = Modifier.padding(contentPadding).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center){
        Icon(
            modifier = Modifier.size(128.dp),
            imageVector = Icons.Default.Close,
            contentDescription = "error ico",
            tint = Color.Red)
        Text(
            modifier = Modifier.padding(16.dp),
            text = error.message ?: stringResource(id = R.string.unknown_api_error),
            textAlign = TextAlign.Center)
        Button(
            modifier = Modifier.testTag(TestTags.RETRY_BUTTON),
            onClick = onRetry) {
            Text(text = stringResource(id = R.string.retry))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CityWeatherScreenContent(content: CityWeatherState.CONTENT, contentPadding: PaddingValues, onBack: () -> Unit) {
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
                    }
                    .testTag(TestTags.DAYS_PAGER),
                pageCount = content.forecast?.dailyForecasts?.size ?: 0,
                pageSpacing = 16.dp,
                contentPadding = PaddingValues(start = 32.dp, end = 32.dp),
                state = pagerState
            ){ pageIndex ->
                val item = content.forecast?.dailyForecasts?.get(pageIndex)
                val pagerOffset by animateFloatAsState( (pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction,
                    label = "OffsetAnimation"
                )
                DayPage(pagerOffset, item, pageIndex)
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
                    }
                    .testTag(TestTags.PREVIOUS_DAY_BUTTON),
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
                    }
                    .testTag(TestTags.NEXT_DAY_BUTTON),
                tint = if ( pagerState.currentPage == ((content.forecast?.dailyForecasts?.size ?: 0)-1) ) Color.LightGray else Color.DarkGray,
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "next day button")
        }
}

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
        .testTag(TestTags.DAY_PAGE+pageIndex)
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
        item?.temperature?.maximum?.getCelsius()?.roundToInt()
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
                        in Int.MIN_VALUE ..10 -> Color.Blue
                        in 10..20 -> Color.Black
                        in 20..Int.MAX_VALUE -> Color.Red
                        else -> Color.White
                    }
                )
            }
        item?.day?.iconPhrase?.let { weatherDescription ->
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
    in 3 .. 5, 20, 21, 31, 32 -> R.drawable.animated_cloudy_sunny_day
    in 6 .. 11, 19 -> R.drawable.animated_cloudy_day
    in 12 .. 18, in 22 .. 29 -> R.drawable.animated_rainy_day
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
    val forecastDailyPreview = DailyForecast(
        "2021-05-01T07:00:00+02:00",
        0,
        Temperature(TemperatureValue(77.0,"K", 0),TemperatureValue(77.0,"F", 0)),
        Day(0, "Sunny", false),
    )

    val forecastPreview = ForecastResponse(
        headline = Headline("",0, 0, "", "", "", 0, "", ""),
        dailyForecasts = listOf(forecastDailyPreview)
    )

    val state = CityWeatherState.CONTENT(forecastPreview)
    CityWeatherScreenContent(state, contentPadding = PaddingValues(0.dp), onBack = {})
}