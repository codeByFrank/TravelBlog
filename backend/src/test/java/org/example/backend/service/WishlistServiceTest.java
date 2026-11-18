package org.example.backend.service;

import org.example.backend.dto.WishlistEntryRequest;
import org.example.backend.dto.WishlistEntryResponse;
import org.example.backend.model.Priority;
import org.example.backend.model.WishlistEntry;
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
    void getAllEntries_returnsEntriesOfUserWithoutUserId() {
        // GIVEN
        WishlistEntry entry = new WishlistEntry("1", USER_ID, "Island", "Nordlichter sehen", Priority.HIGH, "Februar",
                List.of("IS"));
        when(wishlistRepo.findAllByUserId(USER_ID)).thenReturn(List.of(entry));

        // WHEN
        List<WishlistEntryResponse> actual = wishlistService.getAllEntries(USER_ID);

        // THEN
        WishlistEntryResponse expected = new WishlistEntryResponse("1", "Island", "Nordlichter sehen", Priority.HIGH,
                "Februar", List.of("IS"));
        assertEquals(List.of(expected), actual);
        verify(wishlistRepo).findAllByUserId(USER_ID);
    }

    @Test
    void getAllEntries_returnsEmptyCountryList_forEntriesStoredWithoutCountries() {
        // GIVEN: an entry from the time before countries existed has no list at all
        WishlistEntry oldEntry = new WishlistEntry("1", USER_ID, "Island", "Nordlichter sehen", Priority.HIGH, null,
                null);
        when(wishlistRepo.findAllByUserId(USER_ID)).thenReturn(List.of(oldEntry));

        // WHEN
        List<WishlistEntryResponse> actual = wishlistService.getAllEntries(USER_ID);

        // THEN
        assertEquals(List.of(), actual.getFirst().countryCodes());
    }

    @Test
    void addEntry_savesEntryWithUserAndWithoutId() {
        // GIVEN
        WishlistEntryRequest request = new WishlistEntryRequest("Patagonien", "Wandern", Priority.HIGH, "November",
                List.of("AR", "CL"));
        WishlistEntry entryToSave = new WishlistEntry(null, USER_ID, "Patagonien", "Wandern", Priority.HIGH,
                "November", List.of("AR", "CL"));
        when(wishlistRepo.save(entryToSave)).thenReturn(entryToSave.withId("1"));

        // WHEN
        WishlistEntryResponse actual = wishlistService.addEntry(USER_ID, request);

        // THEN
        WishlistEntryResponse expected = new WishlistEntryResponse("1", "Patagonien", "Wandern", Priority.HIGH,
                "November", List.of("AR", "CL"));
        assertEquals(expected, actual);
        verify(wishlistRepo).save(entryToSave);
    }

    @Test
    void addEntry_storesNoBestTravelTime_whenItIsBlank() {
        // GIVEN
        WishlistEntryRequest request = new WishlistEntryRequest("Island", "Nordlichter sehen", Priority.HIGH, "  ",
                List.of("IS"));
        WishlistEntry entryToSave = new WishlistEntry(null, USER_ID, "Island", "Nordlichter sehen", Priority.HIGH,
                null, List.of("IS"));
        when(wishlistRepo.save(entryToSave)).thenReturn(entryToSave.withId("1"));

        // WHEN
        WishlistEntryResponse actual = wishlistService.addEntry(USER_ID, request);

        // THEN
        WishlistEntryResponse expected = new WishlistEntryResponse("1", "Island", "Nordlichter sehen", Priority.HIGH,
                null, List.of("IS"));
        assertEquals(expected, actual);
        verify(wishlistRepo).save(entryToSave);
    }

    @Test
    void addEntry_storesEachCountryOnce_whenACountryIsSentTwice() {
        // GIVEN
        WishlistEntryRequest request = new WishlistEntryRequest("Patagonien", "Wandern", Priority.MEDIUM, null,
                List.of("AR", "CL", "AR"));
        WishlistEntry entryToSave = new WishlistEntry(null, USER_ID, "Patagonien", "Wandern", Priority.MEDIUM, null,
                List.of("AR", "CL"));
        when(wishlistRepo.save(entryToSave)).thenReturn(entryToSave.withId("1"));

        // WHEN
        WishlistEntryResponse actual = wishlistService.addEntry(USER_ID, request);

        // THEN
        assertEquals(List.of("AR", "CL"), actual.countryCodes());
        verify(wishlistRepo).save(entryToSave);
    }

    @Test
    void updateEntry_savesChangedEntry_whenEntryBelongsToUser() {
        // GIVEN
        WishlistEntry existingEntry = new WishlistEntry("1", USER_ID, "Island", "Nordlichter sehen", Priority.HIGH,
                "Februar", List.of("IS"));
        WishlistEntryRequest request = new WishlistEntryRequest("Japan", "Kirschblüte", Priority.MEDIUM, "April",
                List.of("JP"));
        WishlistEntry entryToSave = new WishlistEntry("1", USER_ID, "Japan", "Kirschblüte", Priority.MEDIUM, "April",
                List.of("JP"));
        when(wishlistRepo.findByIdAndUserId("1", USER_ID)).thenReturn(Optional.of(existingEntry));
        when(wishlistRepo.save(entryToSave)).thenReturn(entryToSave);

        // WHEN
        WishlistEntryResponse actual = wishlistService.updateEntry(USER_ID, "1", request);

        // THEN
        WishlistEntryResponse expected = new WishlistEntryResponse("1", "Japan", "Kirschblüte", Priority.MEDIUM,
                "April", List.of("JP"));
        assertEquals(expected, actual);
        verify(wishlistRepo).save(entryToSave);
    }

    @Test
    void updateEntry_removesBestTravelTime_whenRequestHasNone() {
        // GIVEN
        WishlistEntry existingEntry = new WishlistEntry("1", USER_ID, "Island", "Nordlichter sehen", Priority.HIGH,
                "Februar", List.of("IS"));
        WishlistEntryRequest request = new WishlistEntryRequest("Island", "Nordlichter sehen", Priority.HIGH, null,
                List.of("IS"));
        WishlistEntry entryToSave = existingEntry.withBestTravelTime(null);
        when(wishlistRepo.findByIdAndUserId("1", USER_ID)).thenReturn(Optional.of(existingEntry));
        when(wishlistRepo.save(entryToSave)).thenReturn(entryToSave);

        // WHEN
        WishlistEntryResponse actual = wishlistService.updateEntry(USER_ID, "1", request);

        // THEN
        WishlistEntryResponse expected = new WishlistEntryResponse("1", "Island", "Nordlichter sehen", Priority.HIGH,
                null, List.of("IS"));
        assertEquals(expected, actual);
    }

    @Test
    void updateEntry_throwsException_whenEntryDoesNotExistOrBelongsToAnotherUser() {
        // GIVEN
        WishlistEntryRequest request = new WishlistEntryRequest("Japan", "Kirschblüte", Priority.MEDIUM, null,
                List.of("JP"));
        when(wishlistRepo.findByIdAndUserId("1", USER_ID)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(NoSuchElementException.class,
                () -> wishlistService.updateEntry(USER_ID, "1", request));
        verify(wishlistRepo, never()).save(any());
    }

    @Test
    void deleteEntry_deletesEntry_whenEntryBelongsToUser() {
        // GIVEN
        WishlistEntry existingEntry = new WishlistEntry("1", USER_ID, "Island", "Nordlichter sehen", Priority.HIGH,
                null, List.of());
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
