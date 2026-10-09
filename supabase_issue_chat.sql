-- GMailGPU 0.1 Beta: per-issue chat
-- Run this migration once in Supabase Dashboard -> SQL Editor.
-- Access is enforced by RLS: issue owner and admins may read; only the authenticated
-- sender may post, and ordinary users can post only to their own issues.

create table if not exists public.issue_messages (
    id uuid primary key default gen_random_uuid(),
    issue_id uuid not null references public.issues(id) on delete cascade,
    sender_id uuid not null references public.profiles(id) on delete cascade,
    body text not null check (char_length(trim(body)) between 1 and 4000),
    created_at timestamptz not null default now()
);

create index if not exists issue_messages_issue_created_idx
    on public.issue_messages (issue_id, created_at asc);

alter table public.issue_messages enable row level security;

grant select, insert on public.issue_messages to authenticated;

drop policy if exists "issue messages read owner or admin" on public.issue_messages;
create policy "issue messages read owner or admin"
on public.issue_messages for select to authenticated
using (
    public.is_admin()
    or exists (
        select 1 from public.issues i
        where i.id = issue_messages.issue_id
          and i.user_id = auth.uid()
    )
);

drop policy if exists "issue messages insert owner or admin" on public.issue_messages;
create policy "issue messages insert owner or admin"
on public.issue_messages for insert to authenticated
with check (
    sender_id = auth.uid()
    and (
        public.is_admin()
        or exists (
            select 1 from public.issues i
            where i.id = issue_messages.issue_id
              and i.user_id = auth.uid()
        )
    )
);

-- Intentionally no UPDATE or DELETE grants/policies: messages are immutable in this MVP.
