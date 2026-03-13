import com.projectsbynipin.passwordruleengine.dto.ApiResponse
import com.projectsbynipin.passwordruleengine.exception.FailedToCheckPasswordException
import com.projectsbynipin.passwordruleengine.service.PasswordService
import com.projectsbynipin.passwordruleengine.service.impl.PasswordServiceImpl
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.SecureASTCustomizer
import spock.lang.Specification

class PasswordTests extends Specification {

    PasswordService passwordService;

    def setup() {
        CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
        SecureASTCustomizer secureASTCustomizer = new SecureASTCustomizer();
        secureASTCustomizer.setAllowedImports(Collections.emptyList());
        secureASTCustomizer.setAllowedReceiversClasses(
                List.of(
                        String.class,
                        Math.class
                )
        );
        compilerConfiguration.addCompilationCustomizers(secureASTCustomizer);
        passwordService = new PasswordServiceImpl(compilerConfiguration);
        passwordService.scriptPath = "../scripts"
    }

    def "Strong passwords returns true and no messages"() {
        when:
        ApiResponse apiResponse = passwordService.checkPasswordStrength("Abcd1234")
        then:
        apiResponse.message == "Password checked successfully."
        apiResponse.passwordStrong
        apiResponse.data == null
    }

    def "Weak passwords with no uppercase character returns false and one message"() {
        when:
        ApiResponse apiResponse = passwordService.checkPasswordStrength("abcd1234")
        then:
        apiResponse.message == "Password checked successfully."
        !apiResponse.passwordStrong
        apiResponse.data.size() == 1
        apiResponse.data.contains("Password should contain at least one uppercase character.")
    }

    def "Weak passwords with no lowercase character returns false and one message"() {
        when:
        ApiResponse apiResponse = passwordService.checkPasswordStrength("ABCD1234")
        then:
        apiResponse.message == "Password checked successfully."
        !apiResponse.passwordStrong
        apiResponse.data.size() == 1
        apiResponse.data.contains("Password should contain at least one lowercase character.")
    }

    def "Weak passwords with no digit returns false and one message"() {
        when:
        ApiResponse apiResponse = passwordService.checkPasswordStrength("ABCDefgh")
        then:
        apiResponse.message == "Password checked successfully."
        !apiResponse.passwordStrong
        apiResponse.data.size() == 1
        apiResponse.data.contains("Password should contain at least one digit.")
    }

    def "Weak passwords with length less than 8 returns false and one message"() {
        when:
        ApiResponse apiResponse = passwordService.checkPasswordStrength("Abcd123")
        then:
        apiResponse.message == "Password checked successfully."
        !apiResponse.passwordStrong
        apiResponse.data.size() == 1
        apiResponse.data.contains("Password should contain a minimum of 8 characters.")
    }

    def "Weak passwords with length less than 8 and no digit returns false and two messages"() {
        when:
        ApiResponse apiResponse = passwordService.checkPasswordStrength("Abcdefg")
        then:
        apiResponse.message == "Password checked successfully."
        !apiResponse.passwordStrong
        apiResponse.data.size() == 2
        apiResponse.data.contains("Password should contain a minimum of 8 characters.")
        apiResponse.data.contains("Password should contain at least one digit.")
    }

    def "Throws exception when scripts folder is missing"() {
        given:
        passwordService.scriptPath = "../error"
        when:
        passwordService.checkPasswordStrength("Abcd1234")
        then:
        thrown(FailedToCheckPasswordException)
    }

    def "Malicious scripts throw Security Exception"() {
        given: "A script that executes System.exit(0)"
        File testFile = new File("../scripts/security_tester.groovy");
        testFile.text = "System.exit(0)"
        when:
        passwordService.checkPasswordStrength("Abcd1234")
        then:
        thrown(SecurityException)
        cleanup: "Deleting the test file"
        if (testFile.exists()) {
            testFile.delete()
        }
    }
}