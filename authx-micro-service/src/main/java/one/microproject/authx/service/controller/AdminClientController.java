package one.microproject.authx.service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Only Global Admins and Admins for this project are authorized.
 */
@RestController
@RequestMapping(path = "/api/v1/admin/project/clients")
public class AdminClientController {
    //TODO:
    //List clients on project
    //Create client for project
    //Delete client from project (only if not used by any client)
    //Update client on project
    //Change client's credentials
    //Rotate client's keys, remove unused keys
}
