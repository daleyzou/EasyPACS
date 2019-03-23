package org.easy.service;

import org.easy.entity.*;
import org.easy.server.DicomReader;

public interface DBService {

    void buildEntities(DicomReader reader);

    Patient buildPatient(DicomReader reader);

    Study buildStudy(DicomReader reader, Patient patient);

    Series buildSeries(DicomReader reader, Study study);

    Equipment buildEquipment(DicomReader reader, Series series);

    Instance buildInstance(DicomReader reader, Series series);
}
