import android.content.Context
import androidx.lifecycle.ViewModel
import com.recipia.aos.ui.api.SignUpService
import com.recipia.aos.ui.dto.signup.ServerResponseDto
import com.recipia.aos.ui.dto.signup.TokenMemberInfoDto
import com.recipia.aos.ui.dto.signup.TokenResponseDto
import com.recipia.aos.ui.jwt.JwtTokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SignUpViewModel(
    appContext: Context
) : ViewModel() {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8081/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService: SignUpService = retrofit.create(SignUpService::class.java)
    private val jwtTokenManager = JwtTokenManager(appContext.applicationContext) // Application Context 사용

    fun login(
        email: String,
        password: String,
        onLoginSuccess: (String) -> Unit,
        onLoginFailure: () -> Unit
    ) {
        val tokenMemberInfoDto = TokenMemberInfoDto(email, password)
        val call = apiService.login(tokenMemberInfoDto)
        call.enqueue(object : Callback<ServerResponseDto> {

            // 성공시 응답
            override fun onResponse(
                call: Call<ServerResponseDto>,
                response: Response<ServerResponseDto>
            ) {
                if (response.isSuccessful) {
                    val accessToken = response.body()?.result?.accessToken
                    if (accessToken != null) {
                        jwtTokenManager.saveToken(accessToken)
                        onLoginSuccess(accessToken)
                    } else {
                        onLoginFailure()
                    }
                } else {
                    onLoginFailure()
                }
            }
            // 실패시 응답
            override fun onFailure(call: Call<ServerResponseDto>, t: Throwable) {
                onLoginFailure()
            }
        })
    }
}
