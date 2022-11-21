package co.touchlab.kampkit

import co.touchlab.kampkit.db.KaMPKitDb
import co.touchlab.kampkit.vm.BreedViewModel
import com.copperleaf.ballast.debugger.BallastDebuggerClientConnection
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<SqlDriver> {
        AndroidSqliteDriver(
            KaMPKitDb.Schema,
            get(),
            "KampkitDb"
        )
    }

    single<Settings> {
        SharedPreferencesSettings(get())
    }

    single<BallastDebuggerClientConnection<*>> {
        BallastDebuggerClientConnection(
            OkHttp,
            get(),
            host = "10.0.2.2",
        ).also { it.connect(KermitBallastLogger(getWith("Debugger"))) }
    }
    single { OkHttp.create() }

    viewModel { BreedViewModel(get(), get()) }
}
