package codes.monkey.logging

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloWorldController {

    @RequestMapping('/greeting')
    Map greeting(){
        [message:'hello world']
    }


}
