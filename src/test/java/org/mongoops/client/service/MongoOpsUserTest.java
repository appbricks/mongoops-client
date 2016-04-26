package org.mongoops.client.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mongoops.client.Application;
import org.mongoops.client.model.Role;
import org.mongoops.client.model.User;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest
public class MongoOpsUserTest
    extends MongoOpsTestBase {

    private static final String USER_ID = "56556bf6e4b05351c706efd4";
    private static final String USER_NAME = "testuser@somedomain.com";

    @Test
    public void testUserCreation()
        throws Throwable {

        MongoOpsClientService clientService = super.mockService.getClientService();
        MockRestServiceServer mockServer = super.mockService.createServer();

        mockServer.expect(requestTo(clientService.resourceUrl("users")))
            .andExpect(new MockHttpRequest(HttpMethod.POST, "UserOperations", "NewUser_Req"))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("UserOperations", "NewUser_Resp"), MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(clientService.resourceUrl("users", "byName", USER_NAME)))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("UserOperations", "GetUser1"), MediaType.APPLICATION_JSON));

        User user1 = new User();

        List<Role> roles = new ArrayList<>();
        Role role = new Role();
        roles.add(role);
        user1.setRoles(roles);

        user1.setUsername(this.USER_NAME);
        user1.setPassword("634a840b-16de-4536-997f-9d56d51bf010");
        user1.setFirstName("Test");
        user1.setLastName("User");
        user1.setEmailAddress("testuser@somedomain.com");

        role.setGroupId(GROUP_ID);
        role.setRoleName(Role.ROLE_GROUP_READ_ONLY);

        User user2 = clientService.createUser(user1);
        assertEquals(user1.getFirstName(), user2.getFirstName());
        assertEquals(user1.getLastName(), user2.getLastName());
        assertEquals(user1.getEmailAddress(), user2.getEmailAddress());

        User user3 = clientService.getUserByName(user1.getUsername());

        assertEquals(user2.getId(), user3.getId());

        assertEquals(user1.getFirstName(), user3.getFirstName());
        assertEquals(user1.getLastName(), user3.getLastName());
        assertEquals(user1.getEmailAddress(), user3.getEmailAddress());

        mockServer.verify();
    }

    @Test
    public void testUserUpdate()
        throws Throwable {

        MongoOpsClientService clientService = super.mockService.getClientService();
        MockRestServiceServer mockServer = super.mockService.createServer();

        mockServer.expect(requestTo(clientService.resourceUrl("users", "byName", USER_NAME)))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("UserOperations", "GetUser2"), MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(clientService.resourceUrl("users", USER_ID)))
            .andExpect(new MockHttpRequest(HttpMethod.PATCH, "UserOperations", "UpdateUser_Req"))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("UserOperations", "UpdateUser_Resp"), MediaType.APPLICATION_JSON));

        User user = clientService.getUserByName(this.USER_NAME);
        assertTrue(user.getRoles().isEmpty());

        List<Role> roles = new ArrayList<>();
        Role role = new Role();
        roles.add(role);
        user.setRoles(roles);

        role.setGroupId(GROUP_ID);
        role.setRoleName(Role.ROLE_GROUP_READ_ONLY);

        user = clientService.updateUser(user);
        Optional<Role> o = user.getRoles().stream().findFirst();
        assertTrue(o.isPresent());
        assertTrue(o.get().getGroupId().equals(GROUP_ID));
        assertTrue(o.get().getRoleName().equals(Role.ROLE_GROUP_READ_ONLY));

        mockServer.verify();
    }

    @Test
    public void testUserGroupDelete()
        throws Throwable {

        MongoOpsClientService clientService = super.mockService.getClientService();
        MockRestServiceServer mockServer = super.mockService.createServer();

        mockServer.expect(requestTo(clientService.resourceUrl("users", "byName", USER_NAME)))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("UserOperations", "GetUser1"), MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(clientService.resourceUrl("groups", GROUP_ID, "users", USER_ID)))
            .andExpect(method(HttpMethod.DELETE))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("UserOperations", "DeleteUser_Resp"), MediaType.APPLICATION_JSON));
        mockServer.expect(requestTo(clientService.resourceUrl("users", "byName", USER_NAME)))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(MockHttpRequest.mockJsonPayload("UserOperations", "GetUser2"), MediaType.APPLICATION_JSON));

        User user = clientService.getUserByName(this.USER_NAME);
        Optional<Role> o = user.getRoles().stream().findFirst();
        assertTrue(o.isPresent());

        clientService.deleteGroupUser(GROUP_ID, user.getId());

        user = clientService.getUserByName(this.USER_NAME);
        assertTrue(user.getRoles().isEmpty());

        mockServer.verify();
    }
}
