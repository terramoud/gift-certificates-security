package com.epam.esm.config;

import com.epam.esm.domain.entity.Tag;

import java.util.Set;

public class TestTags {
    public TestCertificates testCertificates = new TestCertificates();
    public Tag tag1 = new Tag(1L,
            "language courses",
            Set.of(testCertificates.certificate1,
                    testCertificates.certificate2,
                    testCertificates.certificate5));
    public Tag tag2 = new Tag(2L,
            "dancing courses",
            Set.of(testCertificates.certificate1,
                    testCertificates.certificate2,
                    testCertificates.certificate3,
                    testCertificates.certificate4,
                    testCertificates.certificate5));
    public Tag tag3 = new Tag(3L,
            "diving courses",
            Set.of(testCertificates.certificate2,
                    testCertificates.certificate4,
                    testCertificates.certificate1));
    public Tag tag4 = new Tag(4L,
            "martial arts courses",
            Set.of(testCertificates.certificate1,
                    testCertificates.certificate4,
                    testCertificates.certificate5,
                    testCertificates.certificate10));
    public Tag tag5 = new Tag(5L,
            "driving courses",
            Set.of(testCertificates.certificate1,
                    testCertificates.certificate4));
    public Tag tag6 = new Tag(6L,
            "drawing courses",
            Set.of(testCertificates.certificate1,
                    testCertificates.certificate4,
                    testCertificates.certificate5));
    public Tag tag7 = new Tag(7L,
            "fighting courses",
            Set.of(testCertificates.certificate1,
                    testCertificates.certificate2));
    public Tag tag8 = new Tag(8L,
            "yoga courses",
            Set.of(testCertificates.certificate2,
                    testCertificates.certificate3,
                    testCertificates.certificate5));
    public Tag tag9 = new Tag(9L,
            "airplane flying courses",
            Set.of(testCertificates.certificate2,
                    testCertificates.certificate3));
    public Tag tag10 = new Tag(10L,
            "other courses",
            Set.of(testCertificates.certificate3,
                    testCertificates.certificate5));
    public Tag tag11 = new Tag(11L,
            "swimming courses",
            Set.of(testCertificates.certificate3));
    public Tag tag12 = new Tag(12L,
            "survive courses",
            Set.of(testCertificates.certificate3,
                    testCertificates.certificate5));
    public Tag tag13 = new Tag(13L,
            "math courses",
            Set.of(testCertificates.certificate3));
    public Tag tag14 = new Tag(14L,
            "hunting courses");
    public Tag tag15 = new Tag(15L,
            "new hunting courses",
            Set.of(testCertificates.certificate10));;
}

