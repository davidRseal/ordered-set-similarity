-- TODO: validate all of this and compare to the original java implementation
CREATE OR REPLACE FUNCTION ordered_set_similarity(list1 integer[], list2 integer[])
RETURNS double precision AS $$
DECLARE
    jaccard_similarity double precision;
    kendall_similarity double precision;
    displacement_similarity double precision;
    intersection_size integer;
    union_size integer;
    concordant integer;
    discordant integer;
    total_penalty double precision;
    max_list_size integer;
    max_penalty double precision;
    i integer;
    j integer;
    index_of_i integer;
    index_of_j integer;
    index_map1 integer[];
    index_map2 integer[];
BEGIN
    IF array_length(list1, 1) IS NULL AND array_length(list2, 1) IS NULL THEN
        RETURN 1.0;
    END IF;

    index_map1 := ARRAY(SELECT i FROM generate_subscripts(list1, 1) i);
    index_map2 := ARRAY(SELECT i FROM generate_subscripts(list2, 1) i);

    -- Jaccard Similarity
    intersection_size := array_length(array(SELECT unnest(list1) INTERSECT SELECT unnest(list2)), 1);
    union_size := array_length(list1, 1) + array_length(list2, 1) - intersection_size;
    jaccard_similarity := intersection_size::double precision / union_size;

    -- Kendall's Similarity
    IF intersection_size < 2 THEN
        kendall_similarity := 1.0;
    ELSE
        concordant := 0;
        discordant := 0;
        FOR i IN 1..array_length(list1, 1) - 1 LOOP
            index_of_i := (SELECT index_map2[array_position(list2, list1[i])]);
            IF index_of_i IS NULL THEN
                CONTINUE;
            END IF;
            FOR j IN i + 1..array_length(list1, 1) LOOP
                index_of_j := (SELECT index_map2[array_position(list2, list1[j])]);
                IF index_of_j IS NULL THEN
                    CONTINUE;
                END IF;
                IF index_of_i < index_of_j THEN
                    concordant := concordant + 1;
                ELSE
                    discordant := discordant + 1;
                END IF;
            END LOOP;
        END LOOP;
        kendall_similarity := 0.5 * (1 + (concordant - discordant)::double precision / (0.5 * intersection_size * (intersection_size - 1)));
    END IF;

    -- Displacement Similarity
    max_list_size := GREATEST(array_length(list1, 1), array_length(list2, 1));
    IF max_list_size <= 1 OR intersection_size = 0 THEN
        displacement_similarity := 1.0;
    ELSE
        total_penalty := 0.0;
        FOR i IN 1..array_length(list1, 1) LOOP
            index_of_i := array_position(list2, list1[i]);
            IF index_of_i IS NOT NULL THEN
                total_penalty := total_penalty + ABS((SELECT i - index_of_i));
            END IF;
        END LOOP;

        max_penalty := max_list_size * intersection_size;
        displacement_similarity := 1.0 - (total_penalty / max_penalty);
    END IF;

    RETURN jaccard_similarity * kendall_similarity * displacement_similarity;
END;
$$ LANGUAGE plpgsql;