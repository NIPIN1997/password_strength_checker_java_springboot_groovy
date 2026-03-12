enabled = true
if (password =~ /[A-Z]/) {
    passed = true
} else {
    passed = false
    message = "Password should contain at least one uppercase character."
}