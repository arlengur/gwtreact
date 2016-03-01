UPDATE mproperty as mp
SET required = TRUE
WHERE
    mp.name = 'programName' AND
    EXISTS(SELECT *
           FROM mcontinuousfallcon_mproperty mm
           WHERE mm.parameteridentif_properties_id = mp.id);
