import android.content.Context
import androidx.lifecycle.ViewModel
import com.recipia.aos.ui.api.LoginService
import com.recipia.aos.ui.dto.signup.ServerResponseDto
import com.recipia.aos.ui.dto.signup.TokenMemberInfoDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginViewModel(
    appContext: Context
) : ViewModel() {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8081/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService: LoginService = retrofit.create(LoginService::class.java)
    private val jwtTokenManager = JwtTokenManager(appContext.applicationContext) // Application Context 사용

    // 로그인 함수
    fun login(
        email: String,
        password: String,
        onLoginSuccess: () -> Unit,
        onLoginFailure: (String) -> Unit
    ) {
        // 토큰 정보 세팅하고 api 로그인 요청 실시
        val tokenMemberInfoDto = TokenMemberInfoDto(email, password)
        val call = apiService.login(tokenMemberInfoDto)
        call.enqueue(object : Callback<ServerResponseDto> {

            // 성공적으로 응답이 왔을 때
            override fun onResponse(
                call: Call<ServerResponseDto>,
                response: Response<ServerResponseDto>
            ) {
                if (response.isSuccessful) {
                    response.body()?.result?.let {
                        // 엑세스 토큰, 리프래시 토큰, 멤버id 저장
                        jwtTokenManager.saveAccessToken(it.accessToken)
                        jwtTokenManager.saveRefreshToken(it.refreshToken)
                        jwtTokenManager.saveMemberId(it.memberId)
                        onLoginSuccess() // 엑세스 토큰을 전달하지 않고 콜백만 호출
                    } ?: onLoginFailure("server response fail")
                } else {
                    // 실패 시 서버 응답 메시지 처리
                    val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                    onLoginFailure(errorMsg)
                }
            }

            // 응답 실패 시
            override fun onFailure(
                call: Call<ServerResponseDto>,
                t: Throwable
            ) {
                onLoginFailure(t.message ?: "network error occur")  // 실패 메시지 전달
            }
        })
    }

}
