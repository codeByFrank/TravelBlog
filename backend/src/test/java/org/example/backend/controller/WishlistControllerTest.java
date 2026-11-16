package org.example.backend.controller;

import org.example.backend.model.Priority;
import org.example.backend.model.WishlistEntry;
import org.example.backend.repository.WishlistRepo;
import org.example.backend.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
class WishlistControllerTest {

    private static final String USER_ID = "user-1";
    private static final String OTHER_USER_ID = "user-2";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WishlistRepo wishlistRepo;
    @Autowired
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        wishlistRepo.deleteAll();
    }

    // Creates the Authorization header of a logged-in user.
    private String bearer(String userId) {
        return "Bearer " + jwtService.createToken(userId);
    }

    @Test
    void getAllEntries_returnsUnauthorized_withoutToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/wishlist"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void getAllEntries_returnsOnlyEntriesOfCurrentUser() throws Exception {
        wishlistRepo.save(new WishlistEntry("1", USER_ID, "Island", "Nordlichter sehen", Priority.HIGH));
        wishlistRepo.save(new WishlistEntry("2", OTHER_USER_ID, "Japan", "Kirschblüte", Priority.LOW));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/wishlist")
                        .header("Authorization", bearer(USER_ID)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("""
                        [
                          {
                            "id": "1",
                            "userId": "user-1",
                            "destination": "Island",
                            "reason": "Nordlichter sehen",
                            "priority": "HIGH"
                          }
                        ]
                        """, JsonCompareMode.STRICT));
    }

    @Test
    void getAllEntries_returnsEmptyList_whenNoEntriesExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/wishlist")
                        .header("Authorization", bearer(USER_ID)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]"));
    }

    @Test
    void addEntry_savesEntryAndReturns201() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/wishlist")
                        .header("Authorization", bearer(USER_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "destination": "Island",
                                  "reason": "Nordlichter sehen",
                                  "priority": "HIGH"
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.content().json("""
                        {
                          "userId": "user-1",
                          "destination": "Island",
                          "reason": "Nordlichter sehen",
                          "priority": "HIGH"
                        }
                        """));

        List<WishlistEntry> savedEntries = wishlistRepo.findAllByUserId(USER_ID);
        assertEquals(1, savedEntries.size());
        assertEquals("Island", savedEntries.getFirst().destination());
    }

    @Test
    void addEntry_returns400WithFieldErrors_whenRequiredFieldsAreMissing() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/wishlist")
                        .header("Authorization", bearer(USER_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "destination": "",
                                  "reason": "Nordlichter sehen"
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.destination").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.priority").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.reason").doesNotExist());

        assertTrue(wishlistRepo.findAll().isEmpty());
    }

    @Test
    void updateEntry_changesEntry_whenEntryBelongsToCurrentUser() throws Exception {
        wishlistRepo.save(new WishlistEntry("1", USER_ID, "Island", "Nordlichter sehen", Priority.HIGH));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/wishlist/1")
                        .header("Authorization", bearer(USER_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "destination": "Japan",
                                  "reason": "Kirschblüte",
                                  "priority": "MEDIUM"
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("""
                        {
                          "id": "1",
                          "userId": "user-1",
                          "destination": "Japan",
                          "reason": "Kirschblüte",
                          "priority": "MEDIUM"
                        }
                        """));

        WishlistEntry expected = new WishlistEntry("1", USER_ID, "Japan", "Kirschblüte", Priority.MEDIUM);
        assertEquals(expected, wishlistRepo.findById("1").orElseThrow());
    }

    @Test
    void updateEntry_returns404_whenEntryBelongsToAnotherUser() throws Exception {
        WishlistEntry entryOfOtherUser = new WishlistEntry("1", OTHER_USER_ID, "Island", "Nordlichter sehen", Priority.HIGH);
        wishlistRepo.save(entryOfOtherUser);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/wishlist/1")
                        .header("Authorization", bearer(USER_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "destination": "Japan",
                                  "reason": "Kirschblüte",
                                  "priority": "MEDIUM"
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        assertEquals(entryOfOtherUser, wishlistRepo.findById("1").orElseThrow());
    }

    @Test
    void updateEntry_returns400_whenRequestIsInvalid() throws Exception {
        wishlistRepo.save(new WishlistEntry("1", USER_ID, "Island", "Nordlichter sehen", Priority.HIGH));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/wishlist/1")
                        .header("Authorization", bearer(USER_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "destination": "Japan",
                                  "reason": " ",
                                  "priority": "MEDIUM"
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.reason").exists());
    }

    @Test
    void deleteEntry_deletesEntryAndReturns204() throws Exception {
        wishlistRepo.save(new WishlistEntry("1", USER_ID, "Island", "Nordlichter sehen", Priority.HIGH));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/wishlist/1")
                        .header("Authorization", bearer(USER_ID)))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        assertTrue(wishlistRepo.findById("1").isEmpty());
    }

    @Test
    void deleteEntry_returns404_whenEntryDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/wishlist/unknown")
                        .header("Authorization", bearer(USER_ID)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(404));
    }

    @Test
    void deleteEntry_returns404_whenEntryBelongsToAnotherUser() throws Exception {
        wishlistRepo.save(new WishlistEntry("1", OTHER_USER_ID, "Island", "Nordlichter sehen", Priority.HIGH));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/wishlist/1")
                        .header("Authorization", bearer(USER_ID)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        assertTrue(wishlistRepo.findById("1").isPresent());
    }
}
