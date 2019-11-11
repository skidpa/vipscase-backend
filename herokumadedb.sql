--
-- PostgreSQL database dump
--

-- Dumped from database version 11.5 (Ubuntu 11.5-3.pgdg16.04+1)
-- Dumped by pg_dump version 12.0

-- Started on 2019-11-11 11:26:49

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 197 (class 1259 OID 35164818)
-- Name: customers_id_seq; Type: SEQUENCE; Schema: public; Owner: ypmhwqfqxnnrvl
--

CREATE SEQUENCE public.customers_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.customers_id_seq OWNER TO ypmhwqfqxnnrvl;

SET default_tablespace = '';

--
-- TOC entry 196 (class 1259 OID 35164791)
-- Name: customers; Type: TABLE; Schema: public; Owner: ypmhwqfqxnnrvl
--

CREATE TABLE public.customers (
    id bigint DEFAULT nextval('public.customers_id_seq'::regclass) NOT NULL,
    customername text,
    customerpass text,
    email text,
    lastname text,
    streetname text,
    postcode integer,
    city text,
    birthyear text
);


ALTER TABLE public.customers OWNER TO ypmhwqfqxnnrvl;

--
-- TOC entry 198 (class 1259 OID 35164840)
-- Name: order_details_id_seq; Type: SEQUENCE; Schema: public; Owner: ypmhwqfqxnnrvl
--

CREATE SEQUENCE public.order_details_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.order_details_id_seq OWNER TO ypmhwqfqxnnrvl;

--
-- TOC entry 201 (class 1259 OID 35164945)
-- Name: order_details; Type: TABLE; Schema: public; Owner: ypmhwqfqxnnrvl
--

CREATE TABLE public.order_details (
    id bigint DEFAULT nextval('public.order_details_id_seq'::regclass) NOT NULL,
    order_id bigint,
    product_id bigint,
    status text
);


ALTER TABLE public.order_details OWNER TO ypmhwqfqxnnrvl;

--
-- TOC entry 199 (class 1259 OID 35164859)
-- Name: orders_id_seq; Type: SEQUENCE; Schema: public; Owner: ypmhwqfqxnnrvl
--

CREATE SEQUENCE public.orders_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.orders_id_seq OWNER TO ypmhwqfqxnnrvl;

--
-- TOC entry 202 (class 1259 OID 35164997)
-- Name: orders; Type: TABLE; Schema: public; Owner: ypmhwqfqxnnrvl
--

CREATE TABLE public.orders (
    id bigint DEFAULT nextval('public.orders_id_seq'::regclass) NOT NULL,
    customer_id bigint
);


ALTER TABLE public.orders OWNER TO ypmhwqfqxnnrvl;

--
-- TOC entry 200 (class 1259 OID 35164867)
-- Name: products_id_seq; Type: SEQUENCE; Schema: public; Owner: ypmhwqfqxnnrvl
--

CREATE SEQUENCE public.products_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.products_id_seq OWNER TO ypmhwqfqxnnrvl;

--
-- TOC entry 203 (class 1259 OID 35165163)
-- Name: products; Type: TABLE; Schema: public; Owner: ypmhwqfqxnnrvl
--

CREATE TABLE public.products (
    id bigint DEFAULT nextval('public.products_id_seq'::regclass) NOT NULL,
    productname text,
    "	productdescription" text,
    instock bigint,
    price bigint
);


ALTER TABLE public.products OWNER TO ypmhwqfqxnnrvl;

--
-- TOC entry 3861 (class 0 OID 35164791)
-- Dependencies: 196
-- Data for Name: customers; Type: TABLE DATA; Schema: public; Owner: ypmhwqfqxnnrvl
--

COPY public.customers (id, customername, customerpass, email, lastname, streetname, postcode, city, birthyear) FROM stdin;
\.


--
-- TOC entry 3866 (class 0 OID 35164945)
-- Dependencies: 201
-- Data for Name: order_details; Type: TABLE DATA; Schema: public; Owner: ypmhwqfqxnnrvl
--

COPY public.order_details (id, order_id, product_id, status) FROM stdin;
\.


--
-- TOC entry 3867 (class 0 OID 35164997)
-- Dependencies: 202
-- Data for Name: orders; Type: TABLE DATA; Schema: public; Owner: ypmhwqfqxnnrvl
--

COPY public.orders (id, customer_id) FROM stdin;
\.


--
-- TOC entry 3868 (class 0 OID 35165163)
-- Dependencies: 203
-- Data for Name: products; Type: TABLE DATA; Schema: public; Owner: ypmhwqfqxnnrvl
--

COPY public.products (id, productname, "	productdescription", instock, price) FROM stdin;
\.


--
-- TOC entry 3875 (class 0 OID 0)
-- Dependencies: 197
-- Name: customers_id_seq; Type: SEQUENCE SET; Schema: public; Owner: ypmhwqfqxnnrvl
--

SELECT pg_catalog.setval('public.customers_id_seq', 1, false);


--
-- TOC entry 3876 (class 0 OID 0)
-- Dependencies: 198
-- Name: order_details_id_seq; Type: SEQUENCE SET; Schema: public; Owner: ypmhwqfqxnnrvl
--

SELECT pg_catalog.setval('public.order_details_id_seq', 1, false);


--
-- TOC entry 3877 (class 0 OID 0)
-- Dependencies: 199
-- Name: orders_id_seq; Type: SEQUENCE SET; Schema: public; Owner: ypmhwqfqxnnrvl
--

SELECT pg_catalog.setval('public.orders_id_seq', 1, false);


--
-- TOC entry 3878 (class 0 OID 0)
-- Dependencies: 200
-- Name: products_id_seq; Type: SEQUENCE SET; Schema: public; Owner: ypmhwqfqxnnrvl
--

SELECT pg_catalog.setval('public.products_id_seq', 1, false);


--
-- TOC entry 3727 (class 2606 OID 35164798)
-- Name: customers customers_pkey; Type: CONSTRAINT; Schema: public; Owner: ypmhwqfqxnnrvl
--

ALTER TABLE ONLY public.customers
    ADD CONSTRAINT customers_pkey PRIMARY KEY (id);


--
-- TOC entry 3731 (class 2606 OID 35164952)
-- Name: order_details order_details_pkey; Type: CONSTRAINT; Schema: public; Owner: ypmhwqfqxnnrvl
--

ALTER TABLE ONLY public.order_details
    ADD CONSTRAINT order_details_pkey PRIMARY KEY (id);


--
-- TOC entry 3734 (class 2606 OID 35165001)
-- Name: orders orders_pkey; Type: CONSTRAINT; Schema: public; Owner: ypmhwqfqxnnrvl
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_pkey PRIMARY KEY (id);


--
-- TOC entry 3736 (class 2606 OID 35165170)
-- Name: products products_pkey; Type: CONSTRAINT; Schema: public; Owner: ypmhwqfqxnnrvl
--

ALTER TABLE ONLY public.products
    ADD CONSTRAINT products_pkey PRIMARY KEY (id);


--
-- TOC entry 3728 (class 1259 OID 35165201)
-- Name: fki_order_id_fkey; Type: INDEX; Schema: public; Owner: ypmhwqfqxnnrvl
--

CREATE INDEX fki_order_id_fkey ON public.order_details USING btree (order_id);


--
-- TOC entry 3732 (class 1259 OID 35165049)
-- Name: fki_orders_customerid_fkey; Type: INDEX; Schema: public; Owner: ypmhwqfqxnnrvl
--

CREATE INDEX fki_orders_customerid_fkey ON public.orders USING btree (customer_id);


--
-- TOC entry 3729 (class 1259 OID 35165225)
-- Name: fki_products_id_fkey; Type: INDEX; Schema: public; Owner: ypmhwqfqxnnrvl
--

CREATE INDEX fki_products_id_fkey ON public.order_details USING btree (product_id);


--
-- TOC entry 3737 (class 2606 OID 35165196)
-- Name: order_details order_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ypmhwqfqxnnrvl
--

ALTER TABLE ONLY public.order_details
    ADD CONSTRAINT order_id_fkey FOREIGN KEY (order_id) REFERENCES public.orders(id) NOT VALID;


--
-- TOC entry 3739 (class 2606 OID 35165044)
-- Name: orders orders_customerid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ypmhwqfqxnnrvl
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_customerid_fkey FOREIGN KEY (customer_id) REFERENCES public.customers(id) NOT VALID;


--
-- TOC entry 3738 (class 2606 OID 35165220)
-- Name: order_details products_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ypmhwqfqxnnrvl
--

ALTER TABLE ONLY public.order_details
    ADD CONSTRAINT products_id_fkey FOREIGN KEY (product_id) REFERENCES public.products(id) NOT VALID;


--
-- TOC entry 3874 (class 0 OID 0)
-- Dependencies: 615
-- Name: LANGUAGE plpgsql; Type: ACL; Schema: -; Owner: postgres
--

GRANT ALL ON LANGUAGE plpgsql TO ypmhwqfqxnnrvl;


-- Completed on 2019-11-11 11:27:03

--
-- PostgreSQL database dump complete
--

