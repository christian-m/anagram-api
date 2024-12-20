create table anagram_hashes
(
    id           uuid primary key,
    anagram_hash text not null unique
);

create table anagrams
(
    id              uuid primary key,
    anagram_hash_id uuid references anagram_hashes (id),
    anagram         text not null
);
