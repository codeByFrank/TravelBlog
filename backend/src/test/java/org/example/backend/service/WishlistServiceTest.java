package org.example.backend.service;

import org.example.backend.model.Priority;
import org.example.backend.model.WishlistEntry;
import org.example.backend.dto.WishlistEntryRequest;
import org.example.backend.repository.WishlistRepo;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WishlistServiceTest {

    private static final String USER_ID = "user-1";

    // The repository is a mock: no database is needed for these tests.
    private final WishlistRepo wishlistRepo = mock(WishlistRepo.class);
    private final WishlistService wishlistService = new WishlistService(wishlistRepo);

    @Test
    void getAllEntries_returnsEntriesOfUser() {
        // GIVEN
        WishlistEntry entry = new WishlistEntry("1", USER_ID, "Island", "Nordlichter sehen", Priority.HIGH);
        when(wishlistRepo.findAllByUserId(USER_ID)).thenReturn(List.of(entry));

        // WHEN
        List<WishlistEntry> actual = wishlistService.getAllEntries(USER_ID);

        // THEN
        assertEquals(List.of(entry), actual);
        verify(wishlistRepo).findAllByUserId(USER_ID);
    }

    @Test
    void addEntry_savesEntryWithUserAndWithoutId() {
        // GIVEN
        WishlistEntryRequest request = new WishlistEntryRequest("Island", "Nordlichter sehen", Priority.HIGH);
        WishlistEntry entryToSave = new WishlistEntry(null, USER_ID, "Island", "Nordlichter sehen", Priority.HIGH);
        WishlistEntry savedEntry = entryToSave.withId("1");
        when(wishlistRepo.save(entryToSave)).thenReturn(savedEntry);

        // WHEN
        WishlistEntry actual = wishlistService.addEntry(USER_ID, request);

        // THEN
        assertEquals(savedEntry, actual);
        verify(wishlistRepo).save(entryToSave);
    }

    @Test
    void updateEntry_savesChangedEntry_whenEntryBelongsToUser() {
        // GIVEN
        WishlistEntry existingEntry = new WishlistEntry("1", USER_ID, "Island", "Nordlichter sehen", Priority.HIGH);
        WishlistEntryRequest request = new WishlistEntryRequest("Japan", "Kirschblüte", Priority.MEDIUM);
        WishlistEntry expected = new WishlistEntry("1", USER_ID, "Japan", "Kirschblüte", Priority.MEDIUM);
        when(wishlistRepo.findByIdAndUserId("1", USER_ID)).thenReturn(Optional.of(existingEntry));
        when(wishlistRepo.save(expected)).thenReturn(expected);

        // WHEN
        WishlistEntry actual = wishlistService.updateEntry(USER_ID, "1", request);

        // THEN
        assertEquals(expected, actual);
        verify(wishlistRepo).save(expected);
    }

    @Test
    void updateEntry_throwsException_whenEntryDoesNotExistOrBelongsToAnotherUser() {
        // GIVEN
        WishlistEntryRequest request = new WishlistEntryRequest("Japan", "Kirschblüte", Priority.MEDIUM);
        when(wishlistRepo.findByIdAndUserId("1", USER_ID)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(NoSuchElementException.class,
                () -> wishlistService.updateEntry(USER_ID, "1", request));
        verify(wishlistRepo, never()).save(any());
    }

    @Test
    void deleteEntry_deletesEntry_whenEntryBelongsToUser() {
        // GIVEN
        WishlistEntry existingEntry = new WishlistEntry("1", USER_ID, "Island", "Nordlichter sehen", Priority.HIGH);
        when(wishlistRepo.findByIdAndUserId("1", USER_ID)).thenReturn(Optional.of(existingEntry));

        // WHEN
        wishlistService.deleteEntry(USER_ID, "1");

        // THEN
        verify(wishlistRepo).delete(existingEntry);
    }

    @Test
    void deleteEntry_throwsException_whenEntryDoesNotExistOrBelongsToAnotherUser() {
        // GIVEN
        when(wishlistRepo.findByIdAndUserId("1", USER_ID)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(NoSuchElementException.class,
                () -> wishlistService.deleteEntry(USER_ID, "1"));
        verify(wishlistRepo, never()).delete(any());
    }
}
