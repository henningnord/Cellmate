package no.uio.ifi.in2000.cellmate.ui.consistentcomponents

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.cellmate.domain.usecase.NetworkListener
import no.uio.ifi.in2000.cellmate.domain.usecase.NetworkStatus
import javax.inject.Inject

@HiltViewModel
class NetworkViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val listener = NetworkListener(context)

    private val _networkStatus = MutableStateFlow(NetworkStatus.Available)
    val networkStatus: StateFlow<NetworkStatus> = _networkStatus

    init {
        //Start listening to network status changes
        viewModelScope.launch {
            listener.networkStatus.collect { _ ->
                delay(300) // debounce

                val realStatus = if (isOnline(context)) NetworkStatus.Available else NetworkStatus.Lost
                _networkStatus.value = realStatus
            }
        }

        // Start auto-retry loop to check network status every 10 seconds
        viewModelScope.launch {
            while (true) {
                delay(10000)
                if (_networkStatus.value == NetworkStatus.Lost) {
                    val realStatus = if (isOnline(context)) NetworkStatus.Available else NetworkStatus.Lost
                    _networkStatus.value = realStatus
                }
            }
        }
    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}