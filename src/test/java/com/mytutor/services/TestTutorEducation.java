package com.mytutor.services;

import com.mytutor.dto.tutor.EducationDto;
import com.mytutor.entities.Account;
import com.mytutor.entities.Education;
import com.mytutor.repositories.AccountRepository;
import com.mytutor.repositories.EducationRepository;
import com.mytutor.services.impl.TutorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestTutorEducation {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private EducationRepository educationRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private TutorServiceImpl tutorService;

    private Account tutor;
    private Education education;

    @BeforeEach
    public void setUp() {
        tutor = new Account();
        tutor.setId(1);

        education = new Education();
        education.setAccount(tutor);
    }

    @Test
    public void EducationForm_NNS_69_VerifyUniversity_UniversityMustNotBeBlank() {
        // Arrange
        EducationDto educationDto = new EducationDto();
        educationDto.setUniversityName("");
        educationDto.setDegreeType("BACHELOR");

        when(accountRepository.findById(any(Integer.class))).thenReturn(Optional.of(tutor));
        lenient().when(modelMapper.map(any(EducationDto.class), any(Class.class))).thenReturn(education);

        // Act
        ResponseEntity<?> response = tutorService.addAllEducations(1, Collections.singletonList(educationDto));

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("University name cannot be blank", response.getBody());
    }

    @Test
    public void EducationForm_NNS_70_VerifyUniversity_NumbersAreNotAllowed() {
        // Arrange
        EducationDto educationDto = new EducationDto();
        educationDto.setUniversityName("Uni123");
        educationDto.setDegreeType("BACHELOR");

        when(accountRepository.findById(any(Integer.class))).thenReturn(Optional.of(tutor));
        lenient().when(modelMapper.map(any(EducationDto.class), any(Class.class))).thenReturn(education);

        // Act
        ResponseEntity<?> response = tutorService.addAllEducations(1, Collections.singletonList(educationDto));

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("University name cannot contain numbers or special characters", response.getBody());
    }

    @Test
    public void EducationForm_NNS_71_VerifyUniversity_FirstCharacterCannotHaveSpace() {
        // Arrange
        EducationDto educationDto = new EducationDto();
        educationDto.setUniversityName(" Uni");
        educationDto.setDegreeType("BACHELOR");

        when(accountRepository.findById(any(Integer.class))).thenReturn(Optional.of(tutor));
        lenient().when(modelMapper.map(any(EducationDto.class), any(Class.class))).thenReturn(education);

        // Act
        ResponseEntity<?> response = tutorService.addAllEducations(1, Collections.singletonList(educationDto));

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("University name cannot start with a space", response.getBody());
    }

    @Test
    public void EducationForm_NNS_72_VerifyUniversity_SpecialCharactersNotAllowed() {
        // Arrange
        EducationDto educationDto = new EducationDto();
        educationDto.setUniversityName("Uni@versity!");
        educationDto.setDegreeType("BACHELOR");

        when(accountRepository.findById(any(Integer.class))).thenReturn(Optional.of(tutor));
        lenient().when(modelMapper.map(any(EducationDto.class), any(Class.class))).thenReturn(education);

        // Act
        ResponseEntity<?> response = tutorService.addAllEducations(1, Collections.singletonList(educationDto));

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("University name cannot contain numbers or special characters", response.getBody());
    }

    @Test
    public void EducationForm_NNS_73_VerifyDegree_DropdownContainsValidOptions() {
        // Arrange
        EducationDto educationDto = new EducationDto();
        educationDto.setUniversityName("University");
        educationDto.setDegreeType("INVALID_DEGREE");

        when(accountRepository.findById(any(Integer.class))).thenReturn(Optional.of(tutor));
        lenient().when(modelMapper.map(any(EducationDto.class), any(Class.class))).thenReturn(education);

        // Act
        ResponseEntity<?> response = tutorService.addAllEducations(1, Collections.singletonList(educationDto));

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid degree type", response.getBody());
    }

    @Test
    public void EducationForm_NNS_74_VerifyMajor_MajorMustNotBeBlank() {
        // Arrange
        EducationDto educationDto = new EducationDto();
        educationDto.setUniversityName("University");
        educationDto.setDegreeType("BACHELOR");
        educationDto.setMajorName("");

        when(accountRepository.findById(any(Integer.class))).thenReturn(Optional.of(tutor));
        lenient().when(modelMapper.map(any(EducationDto.class), any(Class.class))).thenReturn(education);

        // Act
        ResponseEntity<?> response = tutorService.addAllEducations(1, Collections.singletonList(educationDto));

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Major name cannot be blank", response.getBody());
    }

    @Test
    public void EducationForm_NNS_75_VerifyMajor_NumbersNotAllowed() {
        // Arrange
        EducationDto educationDto = new EducationDto();
        educationDto.setUniversityName("University");
        educationDto.setDegreeType("BACHELOR");
        educationDto.setMajorName("Computer123");

        when(accountRepository.findById(any(Integer.class))).thenReturn(Optional.of(tutor));
        lenient().when(modelMapper.map(any(EducationDto.class), any(Class.class))).thenReturn(education);

        // Act
        ResponseEntity<?> response = tutorService.addAllEducations(1, Collections.singletonList(educationDto));

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Major name cannot contain numbers or special characters", response.getBody());
    }

    @Test
    public void EducationForm_NNS_76_VerifyMajor_SpecialCharactersNotAllowed() {
        // Arrange
        EducationDto educationDto = new EducationDto();
        educationDto.setUniversityName("University");
        educationDto.setDegreeType("BACHELOR");
        educationDto.setMajorName("Comp!uter");

        when(accountRepository.findById(any(Integer.class))).thenReturn(Optional.of(tutor));
        lenient().when(modelMapper.map(any(EducationDto.class), any(Class.class))).thenReturn(education);

        // Act
        ResponseEntity<?> response = tutorService.addAllEducations(1, Collections.singletonList(educationDto));

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Major name cannot contain numbers or special characters", response.getBody());
    }

    @Test
    public void EducationForm_NNS_77_VerifyMajor_FirstCharacterCannotHaveSpace() {
        // Arrange
        EducationDto educationDto = new EducationDto();
        educationDto.setUniversityName("University");
        educationDto.setDegreeType("BACHELOR");
        educationDto.setMajorName(" Computer Science");

        when(accountRepository.findById(any(Integer.class))).thenReturn(Optional.of(tutor));
        lenient().when(modelMapper.map(any(EducationDto.class), any(Class.class))).thenReturn(education);

        // Act
        ResponseEntity<?> response = tutorService.addAllEducations(1, Collections.singletonList(educationDto));

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Major name cannot start with a space", response.getBody());
    }

    @Test
    public void EducationForm_NNS_78_VerifySpecialization_SpecializationMustNotBeBlank() {
        // Arrange
        EducationDto educationDto = new EducationDto();
        educationDto.setUniversityName("University");
        educationDto.setDegreeType("BACHELOR");
        educationDto.setMajorName("Computer Science");
        educationDto.setSpecialization("");

        when(accountRepository.findById(any(Integer.class))).thenReturn(Optional.of(tutor));
        lenient().when(modelMapper.map(any(EducationDto.class), any(Class.class))).thenReturn(education);

        // Act
        ResponseEntity<?> response = tutorService.addAllEducations(1, Collections.singletonList(educationDto));

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Specialization cannot be blank", response.getBody());
    }

    @Test
    public void EducationForm_NNS_79_VerifySpecialization_NumbersNotAllowed() {
        // Arrange
        EducationDto educationDto = new EducationDto();
        educationDto.setUniversityName("University");
        educationDto.setDegreeType("BACHELOR");
        educationDto.setMajorName("Computer Science");
        educationDto.setSpecialization("AI123");

        when(accountRepository.findById(any(Integer.class))).thenReturn(Optional.of(tutor));
        lenient().when(modelMapper.map(any(EducationDto.class), any(Class.class))).thenReturn(education);

        // Act
        ResponseEntity<?> response = tutorService.addAllEducations(1, Collections.singletonList(educationDto));

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Specialization cannot contain numbers or special characters", response.getBody());
    }

    @Test
    public void EducationForm_NNS_81_VerifySpecialization_SpecialCharactersNotAllowed() {
        // Arrange
        EducationDto educationDto = new EducationDto();
        educationDto.setUniversityName("University");
        educationDto.setDegreeType("BACHELOR");
        educationDto.setMajorName("Computer Science");
        educationDto.setSpecialization("AI!");

        when(accountRepository.findById(any(Integer.class))).thenReturn(Optional.of(tutor));
        lenient().when(modelMapper.map(any(EducationDto.class), any(Class.class))).thenReturn(education);

        // Act
        ResponseEntity<?> response = tutorService.addAllEducations(1, Collections.singletonList(educationDto));

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Specialization cannot contain numbers or special characters", response.getBody());
    }

    @Test
    public void EducationForm_NNS_80_VerifySpecialization_FirstCharacterCannotHaveSpace() {
        // Arrange
        EducationDto educationDto = new EducationDto();
        educationDto.setUniversityName("University");
        educationDto.setDegreeType("BACHELOR");
        educationDto.setMajorName("Computer Science");
        educationDto.setSpecialization(" AI");

        when(accountRepository.findById(any(Integer.class))).thenReturn(Optional.of(tutor));
        lenient().when(modelMapper.map(any(EducationDto.class), any(Class.class))).thenReturn(education);

        // Act
        ResponseEntity<?> response = tutorService.addAllEducations(1, Collections.singletonList(educationDto));

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Specialization cannot start with a space", response.getBody());
    }
    @Test
    public void EducationForm_NNS_82_VerifyStartYear_StartYearMustNotBeBlank() {
        // Arrange
        EducationDto educationDto = new EducationDto();
        educationDto.setUniversityName("University");
        educationDto.setDegreeType("BACHELOR");
        educationDto.setMajorName("Computer Science");
        educationDto.setSpecialization("AI");
        educationDto.setStartYear(0); // Set to 0 to simulate blank

        when(accountRepository.findById(any(Integer.class))).thenReturn(Optional.of(tutor));
        lenient().when(modelMapper.map(any(EducationDto.class), any(Class.class))).thenReturn(education);

        // Act
        ResponseEntity<?> response = tutorService.addAllEducations(1, Collections.singletonList(educationDto));

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Start Year must not be blank", response.getBody());
    }

    @Test
    public void EducationForm_NNS_85_VerifyEndYear_EndYearMustNotBeBlank() {
        // Arrange
        EducationDto educationDto = new EducationDto();
        educationDto.setUniversityName("University");
        educationDto.setDegreeType("BACHELOR");
        educationDto.setMajorName("Computer Science");
        educationDto.setSpecialization("AI");
        educationDto.setStartYear(2020); // Example start year
        educationDto.setEndYear(0); // Set to 0 to simulate blank

        when(accountRepository.findById(any(Integer.class))).thenReturn(Optional.of(tutor));
        lenient().when(modelMapper.map(any(EducationDto.class), any(Class.class))).thenReturn(education);

        // Act
        ResponseEntity<?> response = tutorService.addAllEducations(1, Collections.singletonList(educationDto));

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("End Year must not be blank", response.getBody());
    }
}
