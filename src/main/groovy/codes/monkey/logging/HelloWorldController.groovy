package codes.monkey.logging

import org.perf4j.aop.Profiled
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloWorldController {

    @Profiled(tag="greeting.GET")
    @RequestMapping('/greeting')
    Map greeting(){
        [message:'hello world']
    }


}
